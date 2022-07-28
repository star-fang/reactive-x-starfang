package com.rx.starfang.database.room.rok.relations.many_to_many

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Commander
import com.rx.starfang.database.room.rok.source.Talent
import com.rx.starfang.database.room.rok.cross_ref.CmdrTalentCrossRef

data class TalentWithCmdrs(
    @Embedded val talent: Talent,
    @Relation(
        parentColumn = "talentId",
        entityColumn = "cmdrId",
        associateBy = Junction(CmdrTalentCrossRef::class)
    ) val cmdrs: List<Commander>
)
