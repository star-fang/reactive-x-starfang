package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Equipment(
    @PrimaryKey val eqptId: Long,
    @Embedded val name: LanguagePack?,
    val rarityId: Long?, // todo: make specifications..
    val slotId: Long?,
    val goldNeeded: Int?,
    val level: Int?,
    @Embedded val description: LanguagePack?
)
