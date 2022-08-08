package com.rx.starfang.database.room.rok.cross_ref

import androidx.room.Dao
import androidx.room.Entity
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity(primaryKeys = ["eqptId","attrId"])
data class EqptAttrCrossRef(
    // 'EquipmentAllInclusive.kt'
    val eqptId: Long,
    val attrId: Long,
    var attrValues: List<Double>?
)

@Dao
interface EqptAttrXRefDao: RokBaseDao<EqptAttrCrossRef>
