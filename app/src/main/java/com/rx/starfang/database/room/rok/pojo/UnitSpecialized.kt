package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.SpecialUnit

data class UnitSpecialized(
    @Embedded val unit: SpecialUnit,
    @Relation(
        parentColumn = "baseUnitId",
        entity = UnitTyped::class,
        entityColumn = "id"
    )
    val baseUnit: UnitTyped?
)
