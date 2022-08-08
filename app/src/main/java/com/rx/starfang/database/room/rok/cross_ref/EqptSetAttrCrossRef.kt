package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Dao
import androidx.room.Entity
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(primaryKeys = ["eqptSetId", "attrId"])
data class EqptSetAttrCrossRef(
    val eqptSetId: Long,
    val attrId: Long,
    val setCount: Int,
    val attrValues: List<Double>?
)

@Dao
interface EqptSetAttrXRefDao: RokBaseDao<EqptSetAttrCrossRef>
