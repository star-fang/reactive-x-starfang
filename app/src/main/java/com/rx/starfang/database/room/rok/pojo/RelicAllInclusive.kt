package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.RelicAttrCrossRef
import com.rx.starfang.database.room.rok.source.Attribute
import com.rx.starfang.database.room.rok.source.Commander

data class RelicAllInclusive(

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
        entityColumn = "relicId"
    )
    val attrRefs: List<RelicAttrCrossRef>,

    @Relation(
        parentColumn = "id",
        entity = Commander::class,
        entityColumn = "relicId"
    )
    val commander: Commander
)
