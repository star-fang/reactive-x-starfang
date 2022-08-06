package com.rx.starfang.database.room.rok.source

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Talent(
    @PrimaryKey val id : Long,
    var name: LanguagePack?,
    var position: Int?
)