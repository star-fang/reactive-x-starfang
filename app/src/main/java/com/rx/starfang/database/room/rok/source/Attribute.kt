package com.rx.starfang.database.room.rok.source

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rx.starfang.database.room.rok.LanguagePack
import com.rx.starfang.database.room.rok.RokBaseDao

@Entity
data class Attribute(
    @PrimaryKey val id: Long,
    val form: LanguagePack?
)

@Dao
interface AttrDao: RokBaseDao<Attribute>
