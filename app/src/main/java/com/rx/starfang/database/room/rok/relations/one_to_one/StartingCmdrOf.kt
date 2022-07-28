package com.rx.starfang.database.room.rok.relations.one_to_one

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Civilization
import com.rx.starfang.database.room.rok.source.Commander

data class StartingCmdrOf(
    @Embedded val startingCmdr: Commander,
    @Relation(
        parentColumn = "cmdrId",
        entityColumn = "startingCmdrId"
    )
    val civ: Civilization
)
