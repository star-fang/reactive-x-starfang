package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(indices = [Index("stat_type_kor"), Index("stat_type_eng")])
data class StatType (
    @PrimaryKey val id: Long,
    @Embedded(prefix = "stat_type_") val name: LanguagePack?
        )

@Dao
interface StatTypeDao: RokBaseDao<StatType>
