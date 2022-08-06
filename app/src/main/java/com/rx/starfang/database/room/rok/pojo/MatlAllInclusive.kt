package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Material
import com.rx.starfang.database.room.rok.source.MaterialType
import com.rx.starfang.database.room.rok.source.Rarity

data class MatlAllInclusive(
    @Embedded val matl: Material,
    @Relation(
        parentColumn = "typeId",
        entity = MaterialType::class,
        entityColumn = "id"
    )
    val type: MaterialType,

    @Relation(
        parentColumn = "rarityId",
        entity = Rarity::class,
        entityColumn = "id"
    )
    val rarity: Rarity
)
