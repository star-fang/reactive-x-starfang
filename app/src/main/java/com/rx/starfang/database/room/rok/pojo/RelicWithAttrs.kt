package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.RelicAttrCrossRef
import com.rx.starfang.database.room.rok.entities.Attribute
import com.rx.starfang.database.room.rok.entities.Relic

data class RelicWithAttrs(
    @Embedded val relic: Relic,
    @Relation(
        parentColumn = "id",
        entity = Attribute::class,
        entityColumn = "id",
        associateBy = Junction(
            value = RelicAttrCrossRef::class,
            parentColumn = "relicId",
            entityColumn = "attrId"
        )
    )
    val attrs: List<Attribute>,

    @Relation(
        parentColumn = "id",
        entity = RelicAttrCrossRef::class,
        entityColumn = "relicId",
        projection = ["attrValues"]
    )
    val attrValuesList: List<List<Double>?>
)