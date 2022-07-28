package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Skill(
    @PrimaryKey val id: Long,
    var position: Int,
    @Embedded var name: LanguagePack?,
    var enhanceTargetId: Long?, // relation specified in "EnhancedSkillBy.kt"
    var description: LanguagePack?,
    var isActive: Boolean?,
    var rage: Int?,
    var coefficient: List<List<Int>>?,
    var cmdrId: Long? // specified in "CmdrWithSkills.kt"
)