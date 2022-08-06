package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Unit
import com.rx.starfang.database.room.rok.source.UnitType

data class UnitTyped(
    @Embedded val unit: Unit,
    @Relation(
        parentColumn = "typeId",
        entity = UnitType::class,
        entityColumn = "id"
    )
    val unitType: UnitType
)
