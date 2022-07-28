package com.rx.starfang.database.room.rok.relations.one_to_one

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Commander
import com.rx.starfang.database.room.rok.source.Relic

class RelicAndCmdr(
    @Embedded val relic: Relic,
    @Relation(
        parentColumn = "relicId",
        entityColumn = "relicId"
    )
    val cmdr: Commander
)