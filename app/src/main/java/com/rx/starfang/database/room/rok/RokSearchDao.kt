package com.rx.starfang.database.room.rok

import androidx.room.Dao
import androidx.room.Query
import com.rx.starfang.database.room.rok.pojo.SearchPojo

@Dao
interface RokSearchDao {
    @Query("SELECT id,'Commander' AS type, kor FROM Commander WHERE kor LIKE :nameKor UNION SELECT id, 'Equipment' AS type, kor FROM Equipment WHERE kor LIKE :nameKor UNION SELECT id, 'household' AS type, kor FROM Civilization WHERE kor LIKE :nameKor")
    suspend fun search(nameKor: String): List<SearchPojo>
}