package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Attribute
import com.rx.starfang.database.room.rok.source.Civilization
import com.rx.starfang.database.room.rok.cross_ref.CivAttrCrossRef
import com.rx.starfang.database.room.rok.source.Commander

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
    ) val attrs: List<Attribute>,

    @Relation(
        parentColumn = "id",
        entity = CivAttrCrossRef::class,
        entityColumn = "civId"
    ) val attrRef: List<CivAttrCrossRef>,

    @Relation( // 1:n
        parentColumn = "id",
        entityColumn = "civId"
    )
    val units: List<UnitSpecialized>,

    @Relation( // 1:1
        parentColumn = "startingCmdrId",
        entityColumn = "id"
    )
    val startingCommander: Commander?
)
