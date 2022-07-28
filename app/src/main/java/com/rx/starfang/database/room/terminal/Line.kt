package com.rx.starfang.database.room.terminal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lines")
data class Line(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "timeAdded")
    val timeAdded: Long,
    @ColumnInfo(name = "command")
    val command: String?,
    @ColumnInfo( name = "message")
    val message: String
)