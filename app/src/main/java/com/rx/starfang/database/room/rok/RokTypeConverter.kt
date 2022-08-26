package com.rx.starfang.database.room.rok

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class RokTypeConverter(val gson:Gson = Gson()) {
    @TypeConverter
    fun listOfDoublesToJson(value: List<List<Double>>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun doublesToJson(value: List<Double>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun stringListToJson(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun languagePackToJson(value: LanguagePack?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToListOfDoubles(json: String): List<List<Double>> {
        val resultList = mutableListOf<List<Double>>()
        val arr = gson.fromJson(json,Array<Array<Double>>::class.java)
        arr?.run {
            for (i in arr.indices) {
                resultList.add(arr[i].toList())
            }
        }
        return resultList
    }

    @TypeConverter
    fun jsonToListOfDouble(json: String): List<Double> {
        val doubleList = mutableListOf<Double>()
        val arr = gson.fromJson(json,Array<Double>::class.java)
        arr?.run {
            for(i in arr.indices) {
                doubleList.add(arr[i])
            }
        }
        return doubleList
        //return Gson().fromJson<List<Double>>(json,Array<Double>::class.java).toList()
    }

    @TypeConverter
    fun jsonToList(json: String): List<String> {
        return gson.fromJson(json,Array<String>::class.java).toList()
    }

    @TypeConverter
    fun jsonToLanguagePack(json: String): LanguagePack {
        return gson.fromJson(json, LanguagePack::class.java)
    }
}