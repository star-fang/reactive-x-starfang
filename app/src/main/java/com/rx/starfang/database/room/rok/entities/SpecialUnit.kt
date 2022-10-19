package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(indices = [Index("sp_unit_kor"), Index("sp_unit_eng")])
data class SpecialUnit(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "sp_unit_")
    var name: LanguagePack?,
    var civId: Long, // "CivAllInclusive.kt"
    var baseUnitId: Long?, // "SpecializedUnit.kr"
    var attack: Int,
    var defense: Int,
    var health: Int
)

@Dao
interface SpecialUnitDao: RokBaseDao<SpecialUnit>
