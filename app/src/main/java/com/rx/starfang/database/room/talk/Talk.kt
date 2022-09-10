package com.rx.starfang.database.room.talk

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "memos")
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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMemo(memo: Memo)

    @Query("UPDATE memos SET content = :content, reviser = :reviser, reviseTime = :reviseTime WHERE name = :memoName")
    suspend fun updateMemo(memoName: String, content: String, reviser: String, reviseTime: Long )

    @Query("SELECT * FROM memos GROUP BY creator")
    suspend fun getAllMemos(): List<Memo>

    @Query("SELECT * FROM memos WHERE creator = :writer")
    suspend fun getMyMemos(writer: String): List<Memo>

    @Query("SELECT * FROM memos WHERE `replace`(creator, ' ', '') LIKE '%' || `replace`(:writerName, ' ', '') || '%' ")
    suspend fun searchMemosByCreator(writerName: String): List<Memo>

    @Query("SELECT * FROM memos WHERE `replace`(reviser, ' ', '') LIKE '%' || `replace`(:writerName, ' ', '') || '%' ")
    suspend fun searchMemosByReviser(writerName: String): List<Memo>

    @Transaction
    suspend fun searchMemosByWriter(writerName: String): List<Memo> {
        return searchMemosByCreator(writerName).union(searchMemosByReviser(writerName)).toList()
    }

    @Query("SELECT * FROM memos WHERE `replace`(name, ' ', '') LIKE '%' || `replace`(:memoName, ' ', '') || '%'")
    suspend fun searchMemosContainsName(memoName: String): List<Memo>

    @Query("SELECT * FROM memos WHERE name = :memoName")
    suspend fun searchMemoByByName(memoName: String): Memo?

    @Query("DELETE FROM memos WHERE name = :memoName")
    suspend fun deleteMemo(memoName: String)

    @Transaction
    suspend fun searchAndDeleteMemo(memoName: String): Memo? {
        searchMemoByByName(memoName)?.run {
            deleteMemo(memoName)
            return this
        }
        return null
    }

    @Transaction
    suspend fun insertOrUpdateMemo(memoName: String, content: String, writer: String, time: Long ): Memo? {
        searchMemoByByName(memoName)?.run {
            updateMemo(memoName, content, writer, time)
            return this
        }
        insertMemo(Memo(memoName,content,writer,null,null,time))
        return null
    }

}