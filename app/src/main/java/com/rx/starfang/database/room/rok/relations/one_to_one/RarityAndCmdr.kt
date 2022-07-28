package com.rx.starfang.database.room.rok.relations.one_to_one

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Commander
import com.rx.starfang.database.room.rok.source.Rarity

data class RarityAndCmdr(
    @Embedded val rarity: Rarity,
    @Relation(
        parentColumn = "rarityId",
        entityColumn = "rarityId"
    )
    val cmdr: Commander
)