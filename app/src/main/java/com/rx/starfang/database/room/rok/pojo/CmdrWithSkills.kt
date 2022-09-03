package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.entities.Commander
import com.rx.starfang.database.room.rok.entities.Skill

data class CmdrWithSkills(
    @Embedded val cmdr: Commander,
    @Relation(
        parentColumn = "id",
        entity = Skill::class,
        entityColumn = "cmdrId"
    )
    val skills: List<SkillEnhancing>
)
