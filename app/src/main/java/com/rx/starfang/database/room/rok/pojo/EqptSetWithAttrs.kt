package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.EqptSetAttrCrossRef
import com.rx.starfang.database.room.rok.entities.Attribute
import com.rx.starfang.database.room.rok.entities.EquipmentSet

data class EqptSetWithAttrs(
    @Embedded val eqptSet: EquipmentSet,
    @Relation(
        parentColumn = "id",
        entity = Attribute::class,
        entityColumn = "id",
        associateBy = Junction(
            value = EqptSetAttrCrossRef::class,
            parentColumn = "eqptSetId",
            entityColumn = "attrId"
            )
    )
    val attrs: List<Attribute>?,

    @Relation(
        parentColumn = "id",
        entity = EqptSetAttrCrossRef::class,
        entityColumn = "eqptSetId"
    )
    val attrRefs: List<EqptSetAttrCrossRef>?
)
