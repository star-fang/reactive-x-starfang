package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(indices = [Index("base_unit_kor"), Index("base_unit_eng")])
data class BaseUnit (
        @PrimaryKey val id: Long,
        @Embedded(prefix = "base_unit_")
        var name: LanguagePack?,
        var typeId: Long, // "UnitsOfTypeA.kt"
        var tier: Int,
        var attack: Int,
        var defense: Int,
        var health: Int,
        var marchSpeed: Int,
        var power: Int,
        var description: LanguagePack?
        )

@Dao
interface BaseUnitDao: RokBaseDao<BaseUnit>