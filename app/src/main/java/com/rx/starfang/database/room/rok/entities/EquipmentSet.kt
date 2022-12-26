package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.EqptSetWithAttrs

@Entity(indices = [Index("eqpt_set_kor"), Index("eqpt_set_eng")])
data class EquipmentSet(
    @PrimaryKey val id: Long,
    @Embedded(prefix = "eqpt_set_") val name: LanguagePack?
)

@Dao
interface EqptSetDao: RokBaseDao<EquipmentSet> {

    @Transaction
    @Query("SELECT * FROM EquipmentSet WHERE id = :eqptSetId")
    suspend fun searchEqptSetById( eqptSetId: Long ): EqptSetWithAttrs?
}
