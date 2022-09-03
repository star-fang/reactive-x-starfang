package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class EquipmentSlot(
    @PrimaryKey val id: Long,
    val name: LanguagePack?
)

@Dao
interface EqptSlotDao: RokBaseDao<EquipmentSlot>
