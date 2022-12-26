package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(primaryKeys = ["skillId","noteId"], indices = [Index("noteId")])
data class SkillNoteCrossRef (
    val skillId: Long,
    val noteId: Long
    )

@Dao
interface SkillNoteXRefDao: RokBaseDao<SkillNoteCrossRef>