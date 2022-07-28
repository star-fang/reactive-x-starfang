package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Talent(
    @PrimaryKey val talentId : Long,
    @Embedded var name: LanguagePack?,
    var position: Int?
)