package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.EqptSetWithAttrs

@Entity
data class EquipmentSet(
    @PrimaryKey val id: Long,
    @Embedded val name: LanguagePack?
)

@Dao
interface EqptSetDao: RokBaseDao<EquipmentSet> {

    @Query("SELECT * FROM EquipmentSet WHERE id = :eqptSetId")
    suspend fun searchEqptSetById( eqptSetId: Long ): EqptSetWithAttrs?
}
