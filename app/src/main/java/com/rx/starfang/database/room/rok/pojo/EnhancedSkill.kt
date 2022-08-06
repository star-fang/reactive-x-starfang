package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Skill


data class EnhancedSkill(
    @Embedded val skill: Skill,
    @Relation(
        parentColumn = "id",
        entityColumn = "enhanceTargetId"
    )
    val subject: Skill?
)
