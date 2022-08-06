package com.rx.starfang.database.room.rok.source

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Equipment(
    @PrimaryKey val id: Long,
    val name: LanguagePack?,
    val rarityId: Long?, // 'EqptAllInclusive.kt'
    val slotId: Long?, // 'EqptAllInclusive.kt'
    val setId: Long?, // 'EqptAllInclusive.kt'
    val goldNeeded: Int?,
    val level: Int?,
    val description: LanguagePack?
)
