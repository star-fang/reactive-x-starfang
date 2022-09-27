package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rx.starfang.database.room.rok.cross_ref.SkillNoteCrossRef
import com.rx.starfang.database.room.rok.entities.Skill
import com.rx.starfang.database.room.rok.entities.SkillNote

data class SkillAllInclusive (
    @Embedded
    val skill: Skill,
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
    val enhancingSkill: Skill?,

    @Relation(
        parentColumn = "id",
        entity = SkillNote::class,
        entityColumn = "id",
        associateBy = Junction(
            value = SkillNoteCrossRef::class,
            parentColumn = "skillId",
            entityColumn = "noteId"
        )
    )
    val notes: List<SkillNote>?
    )