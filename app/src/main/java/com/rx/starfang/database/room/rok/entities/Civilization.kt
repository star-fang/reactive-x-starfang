package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.CivAllInclusive

@Entity
data class Civilization(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "civ_") var name: LanguagePack?,
    var comment: LanguagePack?,
    var startingCmdrId: Long // 'civAllInclusive.kt'
    )

@Dao
interface CivDao: RokBaseDao<Civilization> {

    @Query("SELECT * FROM Civilization WHERE id = :civId")
    suspend fun searchCivById(civId: Long): CivAllInclusive?
}