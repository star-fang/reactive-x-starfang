package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Commander
import com.rx.starfang.database.room.rok.source.Rarity

data class CmdrsClassifiedByRarity(
    @Embedded val rarity: Rarity,
    @Relation(
        parentColumn = "id",
        entityColumn = "rarityId"
    )
    val cmdrs: List<Commander>
)