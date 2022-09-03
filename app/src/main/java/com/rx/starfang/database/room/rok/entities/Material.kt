package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class Material(
    @PrimaryKey val id: Long,
    val typeId: Long?, // 'MaterialAllInclusive.kt'
    val rarityId: Long?, // 'MaterialAllInclusive.kt'
    val seconds: Int?
)

@Dao
interface MatlDao: RokBaseDao<Material>
