package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.EqptAttrCrossRef
import com.rx.starfang.database.room.rok.cross_ref.EqptMatlCrossRef
import com.rx.starfang.database.room.rok.entities.*

data class EqptAllInclusive(
    @Embedded val eqpt: Equipment,
    @Relation( // m:n
        parentColumn = "id",
        entity = Attribute::class,
        entityColumn = "id",
        associateBy = Junction(
            value = EqptAttrCrossRef::class,
            parentColumn = "eqptId",
            entityColumn = "attrId"
        )
    ) val attrs: List<Attribute>?,

    @Relation(
        parentColumn = "id",
        entity = EqptAttrCrossRef::class,
        entityColumn = "eqptId",
        projection = ["attrValues"]
    )
    val attrValuesList: List<List<Double>?>,

    @Relation( // m:n
        parentColumn = "id",
        entity = Material::class,
        entityColumn = "id",
        associateBy = Junction(
            value = EqptMatlCrossRef::class,
            parentColumn = "eqptId",
            entityColumn = "matlId")
    ) val matls: List<MatlAllInclusive>?,

    @Relation(
        parentColumn = "id",
        entity = EqptMatlCrossRef::class,
        entityColumn = "eqptId",
        projection = ["matlCount"]
    )
    val matlCounts: List<Int>,

    @Relation( // 1:1
        parentColumn = "slotId",
        entity = EquipmentSlot::class,
        entityColumn = "id"
    )
    val slot: EquipmentSlot?,

    @Relation( // 1:1
        parentColumn = "rarityId",
        entity = Rarity::class,
        entityColumn = "id"
    )
    val rarity: Rarity?,

    @Relation( // 1:1
        parentColumn = "setId",
        entity = EquipmentSet::class,
        entityColumn = "id"
    )
    val eqptSet: EquipmentSet?

)

data class EqptWithSlotAndRarity(
    @Embedded val eqpt: Equipment,
    @Relation( // 1:1
        parentColumn = "slotId",
        entity = EquipmentSlot::class,
        entityColumn = "id"
    )
    val slot: EquipmentSlot?,

    @Relation( // 1:1
        parentColumn = "rarityId",
        entity = Rarity::class,
        entityColumn = "id"
    )
    val rarity: Rarity?
)
