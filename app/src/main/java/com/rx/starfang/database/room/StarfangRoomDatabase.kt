package com.rx.starfang.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rx.starfang.database.room.rok.RokTypeConverter
import com.rx.starfang.database.room.rok.cross_ref.*
import com.rx.starfang.database.room.rok.source.*
import com.rx.starfang.database.room.rok.source.Unit
import com.rx.starfang.database.room.terminal.Line
import com.rx.starfang.database.room.terminal.LineDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [
    Line::class
    , Attribute::class
    , Civilization::class
    , Commander::class
    , Equipment::class
    , EquipmentSet::class
    , EquipmentSlot::class
    , Material::class
    , MaterialType::class
    , Rarity::class
    , Relic::class
    , Skill::class
    , SpecialUnit::class
    , Talent::class
    , Unit::class
    , UnitType::class
    , CivAttrCrossRef::class
    , CmdrTalentCrossRef::class
    , EqptAttrCrossRef::class
    , EqptMatlCrossRef::class
    , EqptSetAttrCrossRef::class
    , RelicAttrCrossRef::class
    //, TestModel::class
                     ]
    , version =  1)
@TypeConverters(RokTypeConverter::class)
abstract class StarfangRoomDatabase : RoomDatabase() {

    abstract fun terminalDao(): LineDao
    abstract fun cmdrDao(): CmdrDao

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
                ).addTypeConverter(RokTypeConverter())
                    .fallbackToDestructiveMigration()
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
                        val lineDao = database.terminalDao()
                        lineDao.deleteAll()

                        val cmdrDao = database.cmdrDao()



                    }
                }
            }
        }
    }

}