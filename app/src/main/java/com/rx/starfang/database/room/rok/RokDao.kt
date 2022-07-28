package com.rx.starfang.database.room.rok

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.rx.starfang.database.room.rok.relations.many_to_many.CmdrWithTalents

@Dao
interface RokDao {
    @Transaction
    @Query("SELECT * FROM Commander WHERE TRIM(kor) = :cmdrName")
    fun getCmdrWithTalents(cmdrName: String): List<CmdrWithTalents>
}