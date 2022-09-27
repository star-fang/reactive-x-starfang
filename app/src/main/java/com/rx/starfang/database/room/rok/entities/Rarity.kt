package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class Rarity(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "rar_") val name: LanguagePack?,
    val value: Int
)

@Dao
interface RarityDao: RokBaseDao<Rarity> {
    @Query("SELECT * FROM Rarity WHERE rar_kor = :rarName")
    suspend fun searchRarityByName(rarName: String): Rarity?
}
