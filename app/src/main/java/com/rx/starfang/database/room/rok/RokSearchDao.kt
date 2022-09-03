package com.rx.starfang.database.room.rok

import androidx.room.Dao
import androidx.room.Query
import com.rx.starfang.database.room.rok.pojo.SearchPojo

@Dao
interface RokSearchDao {
    @Query("SELECT id,'Cmdr' AS type, kor FROM Commander WHERE `replace`(kor, ' ', '') LIKE '%' || :nameKor || '%' "+
            "UNION SELECT id, 'Eqpt' AS type, kor FROM Equipment WHERE `replace`(kor, ' ', '') LIKE '%' || :nameKor || '%' "+
            "UNION SELECT id, 'Civ' AS type, kor FROM Civilization WHERE `replace`(kor, ' ', '') LIKE '%' || :nameKor || '%' ")
    suspend fun search(nameKor: String): List<SearchPojo>
}