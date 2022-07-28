package com.rx.starfang.database.room.rok.relations.many_to_many

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.EqptSetAttrCrossRef
import com.rx.starfang.database.room.rok.source.Attribute
import com.rx.starfang.database.room.rok.source.EquipmentSet

data class EqptSetWithAttrs(
    @Embedded val eqptSet: EquipmentSet,
    @Relation(
        parentColumn = "eqptSetId",
        entityColumn = "attrId",
        associateBy = Junction(EqptSetAttrCrossRef::class)
    ) val attrs: List<Attribute>
)
