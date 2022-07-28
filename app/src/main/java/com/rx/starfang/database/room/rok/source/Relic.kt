package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Relic(
    @PrimaryKey val relicId: Long,
    @Embedded val name: LanguagePack?
)