package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.entities.BaseUnit
import com.rx.starfang.database.room.rok.entities.UnitType

data class UnitTyped(
    @Embedded val baseUnit: BaseUnit,
    @Relation(
        parentColumn = "typeId",
        entity = UnitType::class,
        entityColumn = "id"
    )
    val unitType: UnitType?
)
