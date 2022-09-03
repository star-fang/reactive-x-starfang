package com.rx.starfang.database.room.rok.pojo

import androidx.room.Embedded
import com.rx.starfang.database.room.rok.LanguagePack

data class SearchPojo (
    val id: Long,
    val type: String,
    @Embedded val name: LanguagePack
)

