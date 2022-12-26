package com.rx.starfang.database.room.rok.entities

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.CmdrAllInclusive
import com.rx.starfang.database.room.rok.pojo.CmdrWithRarity
import com.rx.starfang.database.room.rok.pojo.CmdrWithSkills
import com.rx.starfang.database.room.rok.pojo.CmdrWithTalents

@Entity(indices = [Index("cmdr_kor"), Index("cmdr_eng")])
data class Commander(
    @PrimaryKey val id: Long,
    var rarityId: Long, // 'cmdrAllInclusive', 'CmdrsClassifiedByRarity'
    @Embedded(prefix = "cmdr_") var name: LanguagePack?,
    var nickname: LanguagePack?,
    var civId: Long?, // 'cmdrAllInclusive'
    var relicId: Long?, // 'cmdrAllInclusive'
    var seasonLimit: Int?,
    var isPrime: Boolean?
)

@Dao
interface CmdrDao: RokBaseDao<Commander> {

    @Transaction
    @Query("SELECT * FROM Commander WHERE `replace`(cmdr_kor, ' ', '') LIKE '%' || :cmdrName || '%'")
    suspend fun searchCmdrsByName(cmdrName: String): List<CmdrWithRarity>

    @Transaction
    @Query("SELECT * FROM Commander WHERE `replace`(cmdr_kor, ' ', '') LIKE '%' || :cmdrName || '%'")
    suspend fun searchCmdrsWithAllInfoByName(cmdrName: String): List<CmdrAllInclusive>

    @Transaction
    @Query("SELECT * FROM Commander WHERE `replace`(cmdr_kor, ' ', '') LIKE '%' || :cmdrName || '%'")
    suspend fun searchCmdrsWithSkillsByName(cmdrName: String): List<CmdrWithSkills>

    @Transaction
    @Query("SELECT * FROM Commander WHERE id = :cmdrId")
    suspend fun searchCmdrWithAllInfoById(cmdrId: Long): CmdrAllInclusive?

    @Transaction
    @Query("SELECT * FROM Commander WHERE id = :cmdrId")
    suspend fun searchCmdrWithTalentsById(cmdrId: Long): CmdrWithTalents?
}