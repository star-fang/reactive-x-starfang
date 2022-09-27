package com.rx.starfang.database.room.rok

import androidx.room.ColumnInfo

data class LanguagePack(
    @ColumnInfo(index = true)
    val kor: String?,
    val eng: String?
)