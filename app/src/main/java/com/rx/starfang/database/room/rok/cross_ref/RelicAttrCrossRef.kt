package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Dao
import androidx.room.Entity
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(primaryKeys = ["relicId", "attrId"])
data class RelicAttrCrossRef (
    // 'RelicAllInclusive.kt', RelicWithAttrs.kt'
    val relicId: Long,
    val attrId: Long,
    val attrValues: List<Double>?
    )

@Dao
interface RelicAttrXRefDao: RokBaseDao<RelicAttrCrossRef>