package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class SpecialUnit(
    @PrimaryKey val specialUnitId: Long,
    @Embedded var name: LanguagePack?,
    var civId: Long, // relation specified in "CivWithSpecialUnits.kt"
    var baseUnitId: Long?, // specified in "SpecializedUnitFrom.kr"
    var attack: Int,
    var defense: Int,
    var health: Int
)
