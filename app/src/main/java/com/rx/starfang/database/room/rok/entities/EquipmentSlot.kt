package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class EquipmentSlot(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "eqpt_slot_") val name: LanguagePack?
)

@Dao
interface EqptSlotDao: RokBaseDao<EquipmentSlot> {
    @Query("SELECT * FROM EquipmentSlot  WHERE eqpt_slot_kor = :slotName")
    suspend fun searchSlotByName(slotName: String):EquipmentSlot?
}
