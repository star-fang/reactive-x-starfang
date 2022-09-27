package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(primaryKeys = ["civId","attrId"])
data class CivAttrCrossRef(
    // 'CivAllInclusive.kt'
    val civId: Long,
    @ColumnInfo(index = true)
    val attrId: Long,
    val attrValues: List<Double>?
)

@Dao
interface CivAttrXRefDao: RokBaseDao<CivAttrCrossRef>
