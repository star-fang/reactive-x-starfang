package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Unit (
        @PrimaryKey val id: Long,
        @Embedded var name: LanguagePack?,
        var typeId: Long, // 1:m relation "UnitsOfTypeA.kt"
        var tier: Int,
        var attack: Int,
        var defense: Int,
        var health: Int,
        var marchSpeed: Int,
        var power: Int,
        @Embedded var description: LanguagePack?
        )