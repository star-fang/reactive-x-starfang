package com.rx.starfang.database.room.rok.relations.one_to_many

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.UnitType

data class UnitsOfTypeA(
    @Embedded val type: UnitType,
    @Relation(
        parentColumn = "unitTypeId",
        entityColumn = "typeId"
    )
    val units: List<Unit>
)
