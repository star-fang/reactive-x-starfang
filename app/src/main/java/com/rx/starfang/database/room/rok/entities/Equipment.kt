package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.EqptAllInclusive

@Entity
data class Equipment(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "eqpt_") val name: LanguagePack?,
    val rarityId: Long?, // 'EqptAllInclusive.kt'
    val slotId: Long?, // 'EqptAllInclusive.kt'
    val setId: Long?, // 'EqptAllInclusive.kt'
    val goldNeeded: Int?,
    val level: Int?,
    val description: LanguagePack?
)

@Dao
interface EqptDao: RokBaseDao<Equipment> {
    @Query("SELECT * FROM Equipment WHERE eqpt_kor LIKE '%' || :eqptName || '%'")
    suspend fun searchEqptsByName(eqptName: String): List<EqptAllInclusive>

    @Query("SELECT * FROM EQUIPMENT WHERE id = :eqptId")
    suspend fun searchEqptById(eqptId: Long): EqptAllInclusive?
}
