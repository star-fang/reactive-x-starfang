package com.rx.starfang.database.room.terminal

import androidx.room.*

@Entity
data class Memo(
    @PrimaryKey val name: String
    , val content: String
    , val creator: String
    , var reviser: String?
    , var reviseTime: Long?
    , val createTime: Long = System.currentTimeMillis()

)

@Dao
interface MemoDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMemo(memo: Memo)

    @Query("SELECT * FROM Memo GROUP BY creator")
    suspend fun getAllMemo(): List<Memo>

    @Query("SELECT * FROM Memo WHERE `replace`(name, ' ', '') LIKE '%' || `replace`(:memoName, ' ', '') || '%'")
    suspend fun searchMemoByName(memoName: String): List<Memo>
}