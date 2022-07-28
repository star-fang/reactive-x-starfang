package com.rx.starfang.database.room.rok.source

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack

@Entity
data class Commander(
    @PrimaryKey val cmdrId: Long,
    var rarityId: Long, // relation specified in "RarityAndCmdr.kt"
    @Embedded var name: LanguagePack?,
    @Embedded var nickname: LanguagePack?,
    var civId: Long?, // specified in "CivAndCmdr.kt"
    var relicId: Long?, // specified in "RelicAndCmdr.kt"
    var seasonLimit: Int?,
    var isPrime: Boolean?
)