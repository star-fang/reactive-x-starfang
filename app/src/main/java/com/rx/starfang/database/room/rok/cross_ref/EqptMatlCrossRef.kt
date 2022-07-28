package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Entity

@Entity(primaryKeys = ["eqptId", "matlId"])
data class EqptMatlCrossRef(
    val eqptId: Long,
    val matlId: Long,
    val matlCount: Int
)
