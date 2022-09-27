package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Dao
import androidx.room.Entity
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(primaryKeys = ["skillId","noteId"])
data class SkillNoteCrossRef (
    val skillId: Long,
    val noteId: Long
    )

@Dao
interface SkillNoteXRefDao: RokBaseDao<SkillNoteCrossRef>