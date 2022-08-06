package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Entity

@Entity(primaryKeys = ["cmdrId", "talentId"])
data class CmdrTalentCrossRef(
    // 'CmdrAllInclusive.kt
    val cmdrId: Long,
    val talentId: Long
)