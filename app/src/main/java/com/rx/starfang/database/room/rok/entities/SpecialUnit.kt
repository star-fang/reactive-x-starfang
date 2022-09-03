package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class SpecialUnit(
    @PrimaryKey val id: Long,
    var name: LanguagePack?,
    var civId: Long, // "CivAllInclusive.kt"
    var baseUnitId: Long?, // "SpecializedUnit.kr"
    var attack: Int,
    var defense: Int,
    var health: Int
)

@Dao
interface SpecialUnitDao: RokBaseDao<SpecialUnit>
