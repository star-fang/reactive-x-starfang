package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(indices = [Index("tal_kor"), Index("tal_eng")])
data class Talent(
    @PrimaryKey val id : Long,
    @Embedded(prefix = "tal_") var name: LanguagePack?,
    var position: Int?,
    var relatedUnitTypeId: Long?
)

@Dao
interface TalentDao: RokBaseDao<Talent>