package com.rx.starfang.database.room.talk

import androidx.room.*
import kotlinx.coroutines.flow.Flow



@Entity(tableName = "talks")
data class Conversation(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "isUser")
    val isUser: Boolean,
    @ColumnInfo(name = "timeAdded")
    val timeAdded: Long
)

@Dao
interface TalkDao{
    @Query("SELECT * FROM talks ORDER BY timeAdded")
    fun getAllConversations(): Flow<List<Conversation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertConversation(conversation: Conversation)
}