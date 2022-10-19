package com.rx.starfang.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rx.starfang.database.room.memo.Memo
import com.rx.starfang.database.room.memo.MemoDao
import com.rx.starfang.database.room.rok.RokSearchDao
import com.rx.starfang.database.room.rok.RokTypeConverter
import com.rx.starfang.database.room.rok.cross_ref.*
import com.rx.starfang.database.room.rok.entities.*
import com.rx.starfang.database.room.talk.Conversation
import com.rx.starfang.database.room.terminal.Line
import com.rx.starfang.database.room.terminal.LineDao
import com.rx.starfang.database.room.talk.TalkDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Line::class, Conversation::class, Memo::class, Attribute::class, Civilization::class, Commander::class, Equipment::class, EquipmentSet::class, EquipmentSlot::class, Material::class, MaterialType::class, Rarity::class, Relic::class, Skill::class, SpecialUnit::class, Talent::class, BaseUnit::class, UnitType::class, StatType::class, SkillNote::class, CivAttrCrossRef::class, CmdrTalentCrossRef::class, EqptAttrCrossRef::class, EqptMatlCrossRef::class, EqptSetAttrCrossRef::class, RelicAttrCrossRef::class, SkillNoteCrossRef::class
    ], version = 1
)
@TypeConverters(RokTypeConverter::class)
abstract class StarfangRoomDatabase : RoomDatabase() {

    abstract fun lineDao(): LineDao
    abstract fun talkDao(): TalkDao
    abstract fun memoDao(): MemoDao
    abstract fun rokSearchDao(): RokSearchDao
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
    abstract fun skillNoteDao(): SkillNoteDao
    abstract fun specialUnitDao(): SpecialUnitDao
    abstract fun talentDao(): TalentDao
    abstract fun baseUnitDao(): BaseUnitDao
    abstract fun unitTypeDao(): UnitTypeDao
    abstract fun statTypeDao(): StatTypeDao
    abstract fun civAttrXRefDao(): CivAttrXRefDao
    abstract fun cmdrTalentXRefDao(): CmdrTalentXRefDao
    abstract fun eqptAttrXRefDao(): EqptAttrXRefDao
    abstract fun eqptMatlXRefDao(): EqptMatlXRefDao
    abstract fun eqptSetAttrXRefDao(): EqptSetAttrXRefDao
    abstract fun relicAttrXRefDao(): RelicAttrXRefDao
    abstract fun skillNoteXRefDao(): SkillNoteXRefDao

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
                SkillNote::class to skillNoteDao(),
                SpecialUnit::class to specialUnitDao(),
                Talent::class to talentDao(),
                BaseUnit::class to baseUnitDao(),
                UnitType::class to unitTypeDao(),
                StatType::class to statTypeDao(),
                CivAttrCrossRef::class to civAttrXRefDao(),
                CmdrTalentCrossRef::class to cmdrTalentXRefDao(),
                EqptAttrCrossRef::class to eqptAttrXRefDao(),
                EqptMatlCrossRef::class to eqptMatlXRefDao(),
                EqptSetAttrCrossRef::class to eqptSetAttrXRefDao(),
                RelicAttrCrossRef::class to relicAttrXRefDao(),
                SkillNoteCrossRef::class to skillNoteXRefDao()
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

                        val lineDao = database.lineDao()
                        lineDao.deleteAll()
                        lineDao.addLine(Line(0,System.currentTimeMillis(), null, "Database schema version${db.version} loaded"))
                        lineDao.addLine(Line(0,System.currentTimeMillis(), "", ""))


                    }
                }
            }
        }
    }

}