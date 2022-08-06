package com.rx.starfang.database.room.test

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestModel (
    @PrimaryKey val id: Long,
    var name: String,
    var list: List<String> = listOf(),
    var listOfList: List<List<Double>> = listOf(),
    @Embedded var obj: TestEmbeddedModel
    )