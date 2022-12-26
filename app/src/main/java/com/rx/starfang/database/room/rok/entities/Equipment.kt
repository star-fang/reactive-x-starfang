package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.EqptAllInclusive
import com.rx.starfang.database.room.rok.pojo.EqptWithSlotAndRarity

@Entity(indices = [Index("eqpt_kor"), Index("eqpt_eng")])
data class Equipment(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "eqpt_") val name: LanguagePack?,
    val rarityId: Long?, // 'Eqpt.kt'
    val slotId: Long?, // 'Eqpt.kt'
    val setId: Long?, // 'Eqpt.kt'
    val goldNeeded: Int?,
    val level: Int?,
    val description: LanguagePack?
)

@Dao
interface EqptDao: RokBaseDao<Equipment> {
    @Transaction
    @Query("SELECT * FROM Equipment WHERE eqpt_kor LIKE '%' || :eqptName || '%'")
    suspend fun searchEqptsByName(eqptName: String): List<EqptAllInclusive>

    @Transaction
    @Query("SELECT * FROM Equipment WHERE id = :eqptId")
    suspend fun searchEqptById(eqptId: Long): EqptAllInclusive?

    @Transaction
    @Query("SELECT * FROM EQUIPMENT WHERE slotId = :slotId AND rarityId IN (:rarityIds)"
    +" ORDER BY rarityId ASC")
    suspend fun searchEqptsBySlotAndRarity(slotId: Long, rarityIds: List<Long>): List<EqptWithSlotAndRarity>

    @Transaction
    @Query("SELECT * FROM EQUIPMENT WHERE slotId = :slotId"
            +" ORDER BY rarityId ASC")
    suspend fun searchEqptsBySlot(slotId: Long): List<EqptWithSlotAndRarity>

    @Transaction
    suspend fun searchEqptsBySlotAndRarityOrNull(slotId: Long, rarityIds: List<Long>?): List<EqptWithSlotAndRarity> {
        return if(rarityIds == null) searchEqptsBySlot(slotId) else searchEqptsBySlotAndRarity(slotId, rarityIds)
    }
}
