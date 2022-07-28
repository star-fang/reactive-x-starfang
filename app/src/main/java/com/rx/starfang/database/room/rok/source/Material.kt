package com.rx.starfang.database.room.rok.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Material(
    @PrimaryKey val matlId: Long,
    val typeId: Long?, // todo make specifications
    val rarityId: Long?,
    val seconds: Int?
)
