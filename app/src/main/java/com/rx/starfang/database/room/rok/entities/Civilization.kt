package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.CivAllInclusive

@Entity(indices = [Index("civ_kor"), Index("civ_eng")])
data class Civilization(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "civ_") var name: LanguagePack?,
    var comment: LanguagePack?,
    var startingCmdrId: Long // 'civAllInclusive.kt'
    )

@Dao
interface CivDao: RokBaseDao<Civilization> {

    @Transaction
    @Query("SELECT * FROM Civilization WHERE id = :civId")
    suspend fun searchCivById(civId: Long): CivAllInclusive?
}