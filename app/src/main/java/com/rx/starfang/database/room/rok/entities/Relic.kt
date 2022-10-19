package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(indices = [Index("relic_kor"), Index("relic_eng")])
data class Relic(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "relic_") val name: LanguagePack?
)

@Dao
interface RelicDao: RokBaseDao<Relic>