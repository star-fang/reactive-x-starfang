package com.rx.starfang.database.room.rok.relations.one_to_one

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Skill

data class EnhancedSkillBy(
    @Embedded val enhancedSkill: Skill,
    @Relation(
        parentColumn = "skillId",
        entityColumn = "enhanceTargetId"
    )
    val skill: Skill
)
