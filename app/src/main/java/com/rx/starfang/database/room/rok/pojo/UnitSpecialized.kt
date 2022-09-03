package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.entities.BaseUnit
import com.rx.starfang.database.room.rok.entities.SpecialUnit

data class UnitSpecialized(
    @Embedded val unit: SpecialUnit,
    @Relation(
        parentColumn = "baseUnitId",
        entity = BaseUnit::class,
        entityColumn = "id"
    )
    val typedBaseUnit: UnitTyped?
)
