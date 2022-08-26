package com.rx.starfang.database.room.terminal

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LineDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLine(line: Line)

    @Query("SELECT * FROM lines WHERE timeAdded >= :activityTime ORDER BY timeAdded ASC")
    fun getLines(activityTime: Long): Flow<List<Line>>

    @Query("UPDATE lines SET command = :command WHERE id = :id")
    suspend fun updateCommand(id:Long, command: String)

    @Query("UPDATE lines SET message = :message WHERE id = :id")
    suspend fun updateMessage(id:Long, message: String)

    @Delete
    suspend fun deleteLine(line: Line)

    @Query("DELETE FROM lines")
    suspend fun deleteAll()
}