package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(indices = [Index("rar_kor"), Index("rar_eng")])
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
