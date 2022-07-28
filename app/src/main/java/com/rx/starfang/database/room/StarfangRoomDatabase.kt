package com.rx.starfang.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rx.starfang.database.room.terminal.Line
import com.rx.starfang.database.room.terminal.LineDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Line::class], version =  1)
abstract class StarfangRoomDatabase : RoomDatabase() {

    abstract fun terminalDao(): LineDao

    companion object {
        @Volatile
        private var INSTANCE: StarfangRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): StarfangRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StarfangRoomDatabase::class.java,
                    "starfang_room_database"
                ).fallbackToDestructiveMigration()
                    .addCallback(TerminalDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class TerminalDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val dao = database.terminalDao()

                        dao.deleteAll()


                    }
                }
            }
        }
    }

}