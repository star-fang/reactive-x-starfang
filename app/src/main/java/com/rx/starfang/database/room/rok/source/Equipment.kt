package com.rx.starfang.database.room.rok.source

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.EqptAllInclusive

@Entity
data class Equipment(
    @PrimaryKey val id: Long,
    @Embedded val name: LanguagePack?,
    val rarityId: Long?, // 'EqptAllInclusive.kt'
    val slotId: Long?, // 'EqptAllInclusive.kt'
    val setId: Long?, // 'EqptAllInclusive.kt'
    val goldNeeded: Int?,
    val level: Int?,
    val description: LanguagePack?
)

@Dao
interface EqptDao: RokBaseDao<Equipment> {
    @Query("SELECT * FROM Equipment WHERE kor LIKE '%' || :eqptName || '%'")
    suspend fun searchEqptByName(eqptName: String): List<EqptAllInclusive>
}
