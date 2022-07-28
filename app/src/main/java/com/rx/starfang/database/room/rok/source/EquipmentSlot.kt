package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class EquipmentSlot(
    @PrimaryKey val eqptSlotId: Long,
    @Embedded val name: LanguagePack?
)
