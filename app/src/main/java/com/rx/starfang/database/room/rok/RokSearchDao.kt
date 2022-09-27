package com.rx.starfang.database.room.rok

import androidx.room.Dao
import androidx.room.Query
import com.rx.starfang.database.room.rok.pojo.SearchPojo

@Dao
interface RokSearchDao {
    @Query(
        "SELECT id,'Cmdr' AS type, rarityId AS propId FROM Commander WHERE `replace`(cmdr_kor, ' ', '') LIKE '%' || :nameKor || '%' " +
                "UNION SELECT id, 'Eqpt' AS type, slotId AS propId FROM Equipment WHERE `replace`(eqpt_kor, ' ', '') LIKE '%' || :nameKor || '%' " +
                "UNION SELECT id, 'Civ' AS type, NULL AS propId FROM Civilization WHERE `replace`(civ_kor, ' ', '') LIKE '%' || :nameKor || '%' " +
                "UNION SELECT id, 'Rar' AS type, NULL AS propId FROM Rarity WHERE rar_kor = :nameKor " +
                "UNION SELECT id, 'Eqpt_Slot' AS type, NULL AS propId FROM EquipmentSlot WHERE eqpt_slot_kor = :nameKor "
    )
    suspend fun search(nameKor: String): List<SearchPojo>

    @Query(
        "SELECT id,'Civ' AS type FROM Civilization WHERE `replace`(civ_kor, ' ', '') LIKE '%' || :nameKor || '%' " +
                "UNION SELECT id, 'Tal' AS type FROM Talent WHERE `replace`(tal_kor, ' ', '') LIKE '%' || :nameKor || '%' " +
                "UNION SELECT id, 'Rar' AS type FROM Rarity WHERE `replace`(rar_kor, ' ', '') LIKE '%' || :nameKor || '%' "
    )
    suspend fun searchCmdrRelatedInfo(nameKor: String): List<SearchPojo>
}