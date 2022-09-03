package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class Rarity(
    @PrimaryKey val id: Long,
    val name: LanguagePack?,
    val value: Int
)

@Dao
interface RarityDao: RokBaseDao<Rarity>
