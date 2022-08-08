package com.rx.starfang.database.room.rok

import androidx.room.*

@Dao
interface RokBaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<T>?)

    @Update
    suspend fun update(entity: T?)

    @Update
    suspend fun update(entities: List<T>?)

    @Delete
    suspend fun delete(entity: T?)

    @Delete
    suspend fun delete(entities: List<T>?)
}