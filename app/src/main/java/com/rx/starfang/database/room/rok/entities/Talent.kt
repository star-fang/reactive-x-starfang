package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class Talent(
    @PrimaryKey val id : Long,
    @Embedded(prefix = "tal_") var name: LanguagePack?,
    var position: Int?
)

@Dao
interface TalentDao: RokBaseDao<Talent>