package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Entity

@Entity(primaryKeys = ["relicId", "attrId"])
data class RelicAttrCrossRef (
    val relicId: Long,
    val attrId: Long,
    val attrValues: List<Double>?
    )