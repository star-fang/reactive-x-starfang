package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class Skill(
    @PrimaryKey val id: Long,
    var position: Int,
    @Embedded var name: LanguagePack?,
    var enhanceTargetId: Long?, // "EnhancedSkill.kt"
    var description: LanguagePack?,
    var isActive: Boolean?,
    var rage: Int?,
    var coefficient: List<List<Double>>?,
    var cmdrId: Long? // 'CmdrAllInclusive.kt', 'CmdrWithSkills.kt'
)

@Dao
interface SkillDao: RokBaseDao<Skill>