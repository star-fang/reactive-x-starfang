package com.rx.starfang.database.room.rok.source

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

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

@Dao
interface EqptDao: RokBaseDao<Equipment>
