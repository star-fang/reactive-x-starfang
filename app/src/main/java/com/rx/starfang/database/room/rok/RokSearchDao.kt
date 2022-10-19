package com.rx.starfang.database.room.rok

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.rx.starfang.database.room.rok.pojo.KeywordPojo
import com.rx.starfang.database.room.rok.pojo.SearchPojo

@Dao
interface RokSearchDao {
    @Query(
        "SELECT id,'Cmdr' AS type, rarityId AS propId FROM Commander WHERE `replace`(cmdr_kor, ' ', '') LIKE '%' || :nameKor || '%'" +
                " UNION SELECT id, 'Eqpt' AS type, slotId AS propId FROM Equipment WHERE `replace`(eqpt_kor, ' ', '') LIKE '%' || :nameKor || '%'" +
                " UNION SELECT id, 'Civ' AS type, NULL AS propId FROM Civilization WHERE `replace`(civ_kor, ' ', '') LIKE '%' || :nameKor || '%'"
    )
    suspend fun search(nameKor: String): List<SearchPojo>

    @Query(
        "SELECT id, 'Eqpt_Slot' AS type FROM EquipmentSlot WHERE eqpt_slot_kor = :nameKor" +
                " UNION SELECT id, 'Stat_Type' AS type FROM StatType WHERE stat_type_kor = :nameKor" +
                " UNION SELECT id, 'Unit_Type' AS type FROM UnitType WHERE unit_type_kor = :nameKor OR unit_type_alias_kor = :nameKor" +
                " UNION SELECT id, 'Rar' AS type FROM Rarity WHERE rar_kor = :nameKor"
    )
    suspend fun searchKeywords(nameKor: String): List<KeywordPojo>

    @Query(
        "SELECT id,'Civ' AS type FROM Civilization WHERE `replace`(civ_kor, ' ', '') LIKE '%' || :nameKor || '%' " +
                "UNION SELECT id, 'Tal' AS type FROM Talent WHERE `replace`(tal_kor, ' ', '') LIKE '%' || :nameKor || '%' " +
                "UNION SELECT id, 'Rar' AS type FROM Rarity WHERE `replace`(rar_kor, ' ', '') LIKE '%' || :nameKor || '%' "
    )
    suspend fun searchCmdrRelatedInfo(nameKor: String): List<SearchPojo>
}