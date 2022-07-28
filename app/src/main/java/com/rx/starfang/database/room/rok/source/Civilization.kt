package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Civilization(
    @PrimaryKey val civId: Long,
    @Embedded var name: LanguagePack?,
    @Embedded var comment: LanguagePack?,
    var startingCmdrId: Long // relation specified in "StartingCmdrOf.kt"
    )