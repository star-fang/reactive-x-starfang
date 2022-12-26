package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.SkillAllInclusive

@Entity(indices = [Index("skill_kor"), Index("skill_eng")])
data class Skill(
    @PrimaryKey val id: Long,
    var position: Int,
    @Embedded(prefix = "skill_") var name: LanguagePack?,
    var enhanceTargetId: Long?, // "EnhancedSkill.kt"
    var description: LanguagePack?,
    var isActive: Boolean?,
    var rage: Int?,
    var coefficient: List<List<Double>>?,
    var cmdrId: Long? // 'Cmdr.kt', 'CmdrWithSkills.kt'
)

@Dao
interface SkillDao: RokBaseDao<Skill> {
    @Transaction
    @Query("SELECT * FROM Skill WHERE cmdrId = :cmdrId AND position = :pos")
    suspend fun searchOneOfSkillsByCmdrId(cmdrId: Long, pos: Int): SkillAllInclusive?
}