package com.rx.starfang.database.room.rok.entities

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.AttrWithEqpts
import com.rx.starfang.database.room.rok.pojo.EqptWithSlotAndRarity

@Entity
data class Attribute(
    @PrimaryKey val id: Long,
    val form: LanguagePack?,
    val relatedUnitTypeId: Long?,
    val relatedStatTypeId: Long?
)

@Dao
interface AttrDao : RokBaseDao<Attribute> {
    @Transaction
    @Query(
        "SELECT * FROM Attribute WHERE" +
                " relatedUnitTypeId in (:unitTypeIdList)" +
                " AND relatedStatTypeId in (:statTypeIdList)"
    )
    suspend fun searchAttrWithEqptsByUnitTypesAndStatTypes(unitTypeIdList: List<Long>, statTypeIdList: List<Long>): List<AttrWithEqpts>

    @Transaction
    @Query(
        "SELECT * FROM Attribute WHERE  relatedUnitTypeId in (:unitTypeIdList)"
    )
    suspend fun searchAttrWithEqptsByUnitTypes(unitTypeIdList: List<Long>): List<AttrWithEqpts>

    @Transaction
    @Query(
        "SELECT * FROM Attribute WHERE  relatedStatTypeId in (:statTypeIdList)"
    )
    suspend fun searchAttrWithEqptsByStatTypes(statTypeIdList: List<Long>): List<AttrWithEqpts>

    @Transaction
    suspend fun searchAttrWithEqptsInSpecificSlot(slotId:Long, unitTypeIdList: List<Long>?, statTypeIdList: List<Long>?, rarIdList: List<Long>?): List<AttrWithEqpts>? {
        val attrsWithEqpts = mutableListOf<AttrWithEqpts>()
        (if(statTypeIdList == null && unitTypeIdList != null)
            searchAttrWithEqptsByUnitTypes(unitTypeIdList)
        else if(statTypeIdList != null && unitTypeIdList == null)
            searchAttrWithEqptsByStatTypes(statTypeIdList)
        else if(statTypeIdList != null && unitTypeIdList != null)
            searchAttrWithEqptsByUnitTypesAndStatTypes(unitTypeIdList, statTypeIdList)
        else null)?.forEach { attrWithEqpts->
            val eqpts = mutableListOf<EqptWithSlotAndRarity>()
            val attrValuesList = mutableListOf<List<Double>?>()
            for( i in attrWithEqpts.eqpts.indices  ) {
                if(attrWithEqpts.eqpts[i].eqpt.slotId == slotId && (rarIdList == null || attrWithEqpts.eqpts[i].eqpt.rarityId in rarIdList) ) {
                    eqpts.add(attrWithEqpts.eqpts[i])
                    attrValuesList.add(attrWithEqpts.attrValuesList[i])
                }
            }
            if(eqpts.isNotEmpty()) {
                attrsWithEqpts.add(AttrWithEqpts(attrWithEqpts.attr,eqpts, attrValuesList))
            }
        }
        return if(attrsWithEqpts.isEmpty()) null else attrsWithEqpts
    }
}
