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

@Database(
    entities = [
        Line::class, Attribute::class, Civilization::class, Commander::class, Equipment::class, EquipmentSet::class, EquipmentSlot::class, Material::class, MaterialType::class, Rarity::class, Relic::class, Skill::class, SpecialUnit::class, Talent::class, Unit::class, UnitType::class, CivAttrCrossRef::class, CmdrTalentCrossRef::class, EqptAttrCrossRef::class, EqptMatlCrossRef::class, EqptSetAttrCrossRef::class, RelicAttrCrossRef::class
        //, TestModel::class
    ], version = 1
)
@TypeConverters(RokTypeConverter::class)
abstract class StarfangRoomDatabase : RoomDatabase() {

    abstract fun terminalDao(): LineDao
    abstract fun attrDao(): AttrDao
    abstract fun civDao(): CivDao
    abstract fun cmdrDao(): CmdrDao
    abstract fun eqptDao(): EqptDao
    abstract fun eqptSetDao(): EqptSetDao
    abstract fun eqptSlotDao(): EqptSlotDao
    abstract fun matlDao(): MatlDao
    abstract fun matlTypeDao(): MatlTypeDao
    abstract fun rarityDao(): RarityDao
    abstract fun relicDao(): RelicDao
    abstract fun skillDao(): SkillDao
    abstract fun specialUnitDao(): SpecialUnitDao
    abstract fun talentDao(): TalentDao
    abstract fun unitDao(): UnitDao
    abstract fun unitTypeDao(): UnitTypeDao
    abstract fun civAttrXRefDao(): CivAttrXRefDao
    abstract fun cmdrTalentXRefDao(): CmdrTalentXRefDao
    abstract fun eqptAttrXRefDao(): EqptAttrXRefDao
    abstract fun eqptMatlXRefDao(): EqptMatlXRefDao
    abstract fun eqptSetAttrXRefDao(): EqptSetAttrXRefDao
    abstract fun relicAttrXRefDao(): RelicAttrXRefDao

    fun rokDaoMap(): HashMap<Any, Any> {
        return HashMap(
            mapOf(
                Attribute::class to attrDao(),
                Civilization::class to civDao(),
                Commander::class to cmdrDao(),
                Equipment::class to eqptDao(),
                EquipmentSet::class to eqptSetDao(),
                EquipmentSlot::class to eqptSlotDao(),
                Material::class to matlDao(),
                MaterialType::class to matlTypeDao(),
                Rarity::class to rarityDao(),
                Relic::class to relicDao(),
                Skill::class to skillDao(),
                SpecialUnit::class to specialUnitDao(),
                Talent::class to talentDao(),
                Unit::class to unitDao(),
                UnitType::class to unitTypeDao(),
                CivAttrCrossRef::class to civAttrXRefDao(),
                CmdrTalentCrossRef::class to cmdrTalentXRefDao(),
                EqptAttrCrossRef::class to eqptAttrXRefDao(),
                EqptMatlCrossRef::class to eqptMatlXRefDao(),
                EqptSetAttrCrossRef::class to eqptSetAttrXRefDao(),
                RelicAttrCrossRef::class to relicAttrXRefDao()

            )
        )
    }

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


                    }
                }
            }
        }
    }

}