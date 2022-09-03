package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.entities.Commander
import com.rx.starfang.database.room.rok.entities.Skill

data class SkillEnhancing(
    @Embedded val skill: Skill,
    @Relation(
        parentColumn = "enhanceTargetId",
        entity = Skill::class,
        entityColumn = "id"
    )
    val enhancedSkill: Skill?,

    @Relation(
        parentColumn = "id",
        entity = Skill::class,
        entityColumn = "enhanceTargetId"
    )
    val enhancingSkill: Skill?
)
