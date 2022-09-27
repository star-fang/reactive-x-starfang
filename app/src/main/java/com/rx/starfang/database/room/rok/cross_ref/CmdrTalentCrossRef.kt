package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(primaryKeys = ["cmdrId", "talentId"])
data class CmdrTalentCrossRef(
    // 'Cmdr.kt
    val cmdrId: Long,
    @ColumnInfo(index = true)
    val talentId: Long
)

@Dao
interface CmdrTalentXRefDao: RokBaseDao<CmdrTalentCrossRef>