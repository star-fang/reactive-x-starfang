package com.rx.starfang.database.room.rok.source

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class Civilization(
    @PrimaryKey val id: Long,
    var name: LanguagePack?,
    var comment: LanguagePack?,
    var startingCmdrId: Long // 'civAllInclusive.kt'
    )

@Dao
interface CivDao: RokBaseDao<Civilization>