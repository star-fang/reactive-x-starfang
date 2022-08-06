package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rx.starfang.database.room.rok.source.Commander

data class CmdrWithSkills(
    @Embedded val cmdr: Commander,
    @Relation(
        parentColumn = "id",
        entity = EnhancedSkill::class,
        entityColumn = "cmdrId"
    )
    val skills: List<EnhancedSkill>
)
