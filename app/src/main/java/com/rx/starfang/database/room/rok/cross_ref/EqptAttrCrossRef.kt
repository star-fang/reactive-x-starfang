package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Entity

@Entity(primaryKeys = ["eqptId","attrId"])
data class EqptAttrCrossRef(
    val eqptId: Long,
    val attrId: Long,
    var attrValues: List<Double>?
)
