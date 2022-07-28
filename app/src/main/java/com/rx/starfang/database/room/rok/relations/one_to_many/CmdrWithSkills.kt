package com.rx.starfang.database.room.rok.relations.one_to_many

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Commander
import com.rx.starfang.database.room.rok.source.Skill

data class CmdrWithSkills(
    @Embedded val cmdr: Commander,
    @Relation(
        parentColumn = "cmdrId",
        entityColumn = "cmdrId"
    )
    val skills: List<Skill>
)
