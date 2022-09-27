package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class Relic(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "relic_") val name: LanguagePack?
)

@Dao
interface RelicDao: RokBaseDao<Relic>