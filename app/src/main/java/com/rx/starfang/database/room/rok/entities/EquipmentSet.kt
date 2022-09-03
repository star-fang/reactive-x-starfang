package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.EqptSetWithAttrs

@Entity
data class EquipmentSet(
    @PrimaryKey val id: Long,
    val name: LanguagePack?
)

@Dao
interface EqptSetDao: RokBaseDao<EquipmentSet> {

    @Query("SELECT * FROM EquipmentSet WHERE id = :eqptSetId")
    suspend fun searchEqptSetById( eqptSetId: Long ): EqptSetWithAttrs?
}
