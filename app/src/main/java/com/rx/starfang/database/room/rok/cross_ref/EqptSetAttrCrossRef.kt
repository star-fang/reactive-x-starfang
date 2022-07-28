package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Entity

@Entity(primaryKeys = ["eqptSetId", "attrId"])
data class EqptSetAttrCrossRef(
    val eqptSetId: Long,
    val attrId: Long,
    val setCount: Int,
    val attrValues: List<Double>?
)
