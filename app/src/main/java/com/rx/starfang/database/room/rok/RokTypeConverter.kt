package com.rx.starfang.database.room.rok

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class RokTypeConverter {
    @TypeConverter
    fun listOfDoublesToJson(value: List<List<Double>>): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun doublesToJson(value: List<Double>): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun listToJson(value: List<String>): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun languagePackToJson(value: LanguagePack): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToListOfDoubles(json: String): List<List<Double>> {
        return Gson().fromJson<List<List<Double>>>(json,Array<Array<Double>>::class.java).toList()
    }

    @TypeConverter
    fun jsonToListOfDouble(json: String): List<Double> {
        return Gson().fromJson<List<Double>>(json,Array<Double>::class.java).toList()
    }

    @TypeConverter
    fun jsonToList(json: String): List<String> {
        return Gson().fromJson(json,Array<String>::class.java).toList()
    }

    @TypeConverter
    fun jsonToLanguagePack(json: String): LanguagePack {
        return Gson().fromJson(json, LanguagePack::class.java)
    }
}