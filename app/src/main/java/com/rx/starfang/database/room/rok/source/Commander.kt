package com.rx.starfang.database.room.rok.source

import androidx.room.*
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao
import com.rx.starfang.database.room.rok.pojo.CmdrAllInclusive

@Entity
data class Commander(
    @PrimaryKey val id: Long,
    var rarityId: Long, // 'cmdrAllInclusive.kt', 'CmdrsClassifiedByRarity.kt'
    @Embedded var name: LanguagePack?,
    var nickname: LanguagePack?,
    var civId: Long?, // 'cmdrAllInclusive.kt'
    var relicId: Long?, // 'cmdrAllInclusive.kt'
    var seasonLimit: Int?,
    var isPrime: Boolean?
)

@Dao
interface CmdrDao: RokBaseDao<Commander> {

    @Transaction
    @Query("SELECT * FROM Commander WHERE kor = :cmdrName")
    suspend fun searchCmdrsByName(cmdrName: String): List<CmdrAllInclusive>
}