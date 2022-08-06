package com.rx.starfang.database.room.rok.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Material(
    @PrimaryKey val id: Long,
    val typeId: Long?, // 'MaterialAllInclusive.kt'
    val rarityId: Long?, // 'MaterialAllInclusive.kt'
    val seconds: Int?
)
