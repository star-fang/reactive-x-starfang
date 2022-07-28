package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Entity

@Entity(primaryKeys = ["civId","attrId"])
data class CivAttrCrossRef(
    val civId: Long,
    val attrId: Long,
    val attrValues: List<Double>?
)
