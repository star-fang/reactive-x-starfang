package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.entities.Attribute
import com.rx.starfang.database.room.rok.entities.Civilization
import com.rx.starfang.database.room.rok.cross_ref.CivAttrCrossRef
import com.rx.starfang.database.room.rok.entities.Commander
import com.rx.starfang.database.room.rok.entities.SpecialUnit

data class CivAllInclusive(
    @Embedded val civ: Civilization,

    @Relation( // m:n
        parentColumn = "id",
        entity = Attribute::class,
        entityColumn = "id",
        associateBy = Junction(
            value = CivAttrCrossRef::class,
            parentColumn = "civId",
            entityColumn = "attrId"
        )
    ) val attrs: List<Attribute>?,

    @Relation(
        parentColumn = "id",
        entity = CivAttrCrossRef::class,
        entityColumn = "civId",
        projection = ["attrValues"]
    ) val attrValuesList: List<List<Double>?>,

    @Relation( // 1:n
        entity = SpecialUnit::class,
        parentColumn = "id",
        entityColumn = "civId"
    )
    val specialUnits: List<UnitSpecialized>,

    @Relation( // 1:1
        parentColumn = "startingCmdrId",
        entity = Commander::class,
        entityColumn = "id"
    )
    val startingCommander: Commander?
)
