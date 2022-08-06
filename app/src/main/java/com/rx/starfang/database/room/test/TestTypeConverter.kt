package com.rx.starfang.database.room.test

import android.util.Log
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class TestTypeConverter(private val gson: Gson) {
    @TypeConverter
    fun listOfListToJson(value: List<List<Double>>): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToListOfList(json: String): List<List<Double>> {
        return gson.fromJson<List<List<Double>>?>(json,Array<Array<Double>>::class.java).toList()
    }

    @TypeConverter
    fun listToJson(value: List<String>): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToList(json: String): List<String> {
        return gson.fromJson(json,Array<String>::class.java).toList()
    }

    @TypeConverter
    fun embeddedToJson(value: TestEmbeddedModel): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToEmbedded(json: String): TestEmbeddedModel {
        return gson.fromJson(json, TestEmbeddedModel::class.java)
    }


}