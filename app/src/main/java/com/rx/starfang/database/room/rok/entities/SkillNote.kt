package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class SkillNote (
    @PrimaryKey val id: Long,
    @Embedded
    val name: LanguagePack?,
    val description: LanguagePack?
    )

@Dao
interface SkillNoteDao: RokBaseDao<SkillNote>