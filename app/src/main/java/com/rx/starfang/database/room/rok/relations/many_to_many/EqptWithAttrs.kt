package com.rx.starfang.database.room.rok.relations.many_to_many

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.EqptAttrCrossRef
import com.rx.starfang.database.room.rok.source.Attribute
import com.rx.starfang.database.room.rok.source.Equipment

data class EqptWithAttrs(
    @Embedded val eqpt: Equipment,
    @Relation(
        parentColumn = "eqptId",
        entityColumn = "attrId",
        associateBy = Junction(EqptAttrCrossRef::class)
    ) val attrs: List<Attribute>
)