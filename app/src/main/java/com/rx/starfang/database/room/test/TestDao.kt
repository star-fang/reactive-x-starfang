package com.rx.starfang.database.room.test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface TestDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntity(model: TestModel)
}