package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Commander
import com.rx.starfang.database.room.rok.source.Skill

data class SkillAllInclusive(
    @Embedded val skill: Skill,
    @Relation(
        parentColumn = "id",
        entityColumn = "enhanceTargetId"
    )
    val subject: Skill? = null,
    @Relation(
        parentColumn = "commanderId",
        entityColumn = "id"
    )
    val commander: Commander
)
