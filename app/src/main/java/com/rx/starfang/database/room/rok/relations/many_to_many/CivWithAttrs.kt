package com.rx.starfang.database.room.rok.relations.many_to_many

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Attribute
import com.rx.starfang.database.room.rok.source.Civilization
import com.rx.starfang.database.room.rok.cross_ref.CivAttrCrossRef

data class CivWithAttrs(
    @Embedded val civ: Civilization,
    @Relation(
        parentColumn = "civId",
        entityColumn = "attrId",
        associateBy = Junction(CivAttrCrossRef::class)
    ) val attr: Attribute
)
