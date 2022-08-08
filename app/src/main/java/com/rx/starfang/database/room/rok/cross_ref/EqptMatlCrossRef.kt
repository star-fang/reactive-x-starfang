package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Dao
import androidx.room.Entity
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(primaryKeys = ["eqptId", "matlId"])
data class EqptMatlCrossRef(
    val eqptId: Long,
    val matlId: Long,
    val matlCount: Int
)

@Dao
interface EqptMatlXRefDao: RokBaseDao<EqptMatlCrossRef>
