package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

data class Rarity(
    @PrimaryKey val id: Long,
    @Embedded val name: LanguagePack?,
    val value: Int
)
