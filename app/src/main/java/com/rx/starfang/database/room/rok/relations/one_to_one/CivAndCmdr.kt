package com.rx.starfang.database.room.rok.relations.one_to_one

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Civilization
import com.rx.starfang.database.room.rok.source.Commander

data class CivAndCmdr(
    @Embedded val civ: Civilization,
    @Relation(
        parentColumn = "civId",
        entityColumn = "civId"
    )
    val cmdr: Commander
)