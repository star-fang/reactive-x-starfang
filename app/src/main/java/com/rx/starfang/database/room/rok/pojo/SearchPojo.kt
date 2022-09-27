package com.rx.starfang.database.room.rok.pojo

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.rx.starfang.database.room.rok.LanguagePack

data class SearchPojo (
    @ColumnInfo(index = true) val id: Long,
    val type: String,
    val propId: Long?
)

