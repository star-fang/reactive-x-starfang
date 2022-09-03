package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.entities.Commander
import com.rx.starfang.database.room.rok.entities.Talent
import com.rx.starfang.database.room.rok.cross_ref.CmdrTalentCrossRef

data class TalentWithCmdrs(
    @Embedded val talent: Talent,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CmdrTalentCrossRef::class,
            parentColumn = "talentId",
            entityColumn = "cmdrId",
            )
    ) val cmdrs: List<Commander>
)
