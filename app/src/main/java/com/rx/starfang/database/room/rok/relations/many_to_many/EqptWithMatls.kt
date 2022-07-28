package com.rx.starfang.database.room.rok.relations.many_to_many

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.EqptMatlCrossRef
import com.rx.starfang.database.room.rok.source.Equipment
import com.rx.starfang.database.room.rok.source.Material

data class EqptWithMatls(
    @Embedded val eqpt: Equipment,
    @Relation(
        parentColumn = "eqptId",
        entityColumn = "matlId",
        associateBy = Junction(EqptMatlCrossRef::class)
    ) val matls: List<Material>
)
