package com.rx.starfang.database.room.rok.source

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class Unit (
        @PrimaryKey val id: Long,
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
interface UnitDao: RokBaseDao<Unit>