package com.rx.starfang.database.room.rok.relations.one_to_one

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.SpecialUnit
import com.rx.starfang.database.room.rok.source.Unit

data class SpecializedUnitFrom(
    @Embedded val unit: Unit,
    @Relation(
        parentColumn = "unitId",
        entityColumn = "baseUnitId"
    )
    val specialUnit: SpecialUnit
)
