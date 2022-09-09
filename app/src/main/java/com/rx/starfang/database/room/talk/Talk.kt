package com.rx.starfang.database.room.talk

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Memo(
    @PrimaryKey val name: String
    , val content: String
    , val creator: String
    , var reviser: String?
    , var reviseTime: Long?
    , val createTime: Long = System.currentTimeMillis()
)

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMemo(memo: Memo)

    @Query("SELECT * FROM Memo GROUP BY creator")
    suspend fun getAllMemo(): List<Memo>

    @Query("SELECT * FROM Memo WHERE `replace`(name, ' ', '') LIKE '%' || `replace`(:memoName, ' ', '') || '%'")
    suspend fun searchMemoByName(memoName: String): List<Memo>
}