package com.rx.starfang.database.room.rok.source

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Skill(
    @PrimaryKey val id: Long,
    var position: Int,
    var name: LanguagePack?,
    var enhanceTargetId: Long?, // "EnhancedSkill.kt"
    var description: LanguagePack?,
    var isActive: Boolean?,
    var rage: Int?,
    var coefficient: List<List<Double>>?,
    var cmdrId: Long? // 'CmdrAllInclusive.kt', 'CmdrWithSkills.kt'
)