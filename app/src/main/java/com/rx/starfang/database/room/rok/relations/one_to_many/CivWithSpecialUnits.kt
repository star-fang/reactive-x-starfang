package com.rx.starfang.database.room.rok.relations.one_to_many

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Civilization
import com.rx.starfang.database.room.rok.source.SpecialUnit

data class CivWithSpecialUnits(
    @Embedded val civ: Civilization,
    @Relation(
        parentColumn = "civId",
        entityColumn = "civId"
    )
    val specialUnits: List<SpecialUnit>
)
