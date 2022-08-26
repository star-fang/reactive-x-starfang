package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.EqptAttrCrossRef
import com.rx.starfang.database.room.rok.cross_ref.EqptMatlCrossRef
import com.rx.starfang.database.room.rok.source.Attribute
import com.rx.starfang.database.room.rok.source.Equipment
import com.rx.starfang.database.room.rok.source.EquipmentSlot
import com.rx.starfang.database.room.rok.source.Rarity

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
        entityColumn = "eqptId"
    )
    val attrRefs: List<EqptAttrCrossRef>?,

    @Relation( // m:n
        parentColumn = "id",
        entity = MatlAllInclusive::class,
        entityColumn = "id",
        associateBy = Junction(
            value = EqptMatlCrossRef::class,
            parentColumn = "eqptId",
            entityColumn = "matlId",)
    ) val matls: List<MatlAllInclusive>?,

    @Relation(
        parentColumn = "id",
        entity = EqptMatlCrossRef::class,
        entityColumn = "eqptId"
    )
    val matlRefs: List<EqptMatlCrossRef>?,

    @Relation( // 1:1
        parentColumn = "slotId",
        entity = EquipmentSlot::class,
        entityColumn = "id"
    )
    val slot: EquipmentSlot?,

    @Relation( // 1:1
        parentColumn = "rarityID",
        entity = Rarity::class,
        entityColumn = "id"
    )
    val rarity: Rarity?,

    @Relation( // 1:1
        parentColumn = "setId",
        entity = EqptSetWithAttrs::class,
        entityColumn = "id"
    )
    val eqptSet: EqptSetWithAttrs?

)
