package com.rx.starfang.database.room.rok.pojo

import androidx.room.*
import com.rx.starfang.database.room.rok.cross_ref.CmdrTalentCrossRef
import com.rx.starfang.database.room.rok.entities.*

data class CmdrAllInclusive (
    @Embedded val cmdr: Commander,
    @Relation(
        parentColumn = "id",
        entity = Skill::class,
        entityColumn = "cmdrId"
    )
    val enhancingSkills: List<SkillEnhancing>?,

    @Relation( // m:n
        parentColumn = "id",
        entity = Talent::class,
        entityColumn = "id",
        associateBy = Junction(
            value = CmdrTalentCrossRef::class,
            parentColumn = "cmdrId",
            entityColumn = "talentId"
        )
    ) val talents: List<Talent>?,

    @Relation( // 1:1
        parentColumn = "civId",
        entityColumn = "id"
    )
    val civ: Civilization?,

    @Relation( // 1:1
        parentColumn = "rarityId",
        entityColumn = "id"
    )
    val rarity: Rarity?,

    @Relation( // 1:1
        parentColumn = "relicId",
        entity = Relic::class,
        entityColumn = "id",
    )
    val relic: RelicWithAttrs?
)