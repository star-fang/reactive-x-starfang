package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.EqptAttrCrossRef
import com.rx.starfang.database.room.rok.entities.Attribute
import com.rx.starfang.database.room.rok.entities.Equipment

data class AttrWithEqpts(
    @Embedded
    val attr: Attribute,
    @Relation( // m:n
        parentColumn = "id",
        entity = Equipment::class,
        entityColumn = "id",
        associateBy = Junction(
            value = EqptAttrCrossRef::class,
            parentColumn = "attrId",
            entityColumn = "eqptId"
        )
    ) val eqpts: List<EqptWithSlotAndRarity>,

    @Relation(
        parentColumn = "id",
        entity = EqptAttrCrossRef::class,
        entityColumn = "attrId",
        projection = ["attrValues"]
    )
    val attrValuesList: List<List<Double>?>,

    /*
    @Relation(
        parentColumn = "id",
        entity = Equipment::class,
        entityColumn = "id",
        associateBy = Junction(
            value = EqptSetAttrCrossRef::class,
            parentColumn = "attrId",
            entityColumn = "eqptSetId"
        )
    ) val eqptSets: List<EquipmentSet>?,

    @Relation(
        parentColumn = "id",
        entity = EqptSetAttrCrossRef::class,
        entityColumn = "attrId"
    )
    val eqptSetRefs: List<EqptSetAttrCrossRef>?,
     */
)