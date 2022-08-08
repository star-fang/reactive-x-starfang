package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Dao
import androidx.room.Entity
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(primaryKeys = ["cmdrId", "talentId"])
data class CmdrTalentCrossRef(
    // 'CmdrAllInclusive.kt
    val cmdrId: Long,
    val talentId: Long
)

@Dao
interface CmdrTalentXRefDao: RokBaseDao<CmdrTalentCrossRef>