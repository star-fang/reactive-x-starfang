package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(indices = [Index("unit_type_kor"), Index("unit_type_eng")
    , Index("unit_type_alias_kor"), Index("unit_type_alias_eng")])
data class UnitType(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "unit_type_")
    val name: LanguagePack?,
    @Embedded(prefix = "unit_type_alias_")
    val alias: LanguagePack?
)

@Dao
interface UnitTypeDao: RokBaseDao<UnitType>
