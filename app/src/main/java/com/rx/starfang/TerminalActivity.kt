package com.rx.starfang

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.rx.starfang.database.room.terminal.Line
import com.rx.starfang.databinding.ActivityTerminalBinding
import com.rx.starfang.ui.list.adapter.LineAdapter
import com.rx.starfang.ui.terminal.TerminalViewModel
import com.rx.starfang.ui.terminal.TerminalViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.Type
import kotlin.collections.HashSet
import kotlin.reflect.KClass

class TerminalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTerminalBinding

    companion object {
        const val ACTION_ADD_LINE = "ACTION_ADD_LINE"
        const val EXTRAS_ADD_LINE_ID = "EXTRAS_ADD_LINE_ID"
        const val EXTRAS_ADD_LINE_COMMAND = "EXTRAS_ADD_LINE_COMMAND"
        const val CMD_NOTIFICATION = "알림"
        const val CMD_CONNECTION = "연결"
    }

    private val terminalViewModel: TerminalViewModel by viewModels {
        TerminalViewModelFactory(
            (application as RxStarfangApp).terminalRepository,
            (application as RxStarfangApp).rokRepository
        )
    }

    fun cmdTask(id: Long, command: String?) {
        Observable.fromCallable {
            return@fromCallable when (command) {
                CMD_NOTIFICATION -> "notification setting activity launched"
                CMD_CONNECTION -> {
                    //"RoK_FangDB_4.0.json"
                    getJsonFromAsset(this, "RoK_FangDB.json")?.run {
                        return@fromCallable jsonToDatabase(this)

                    }
                    "abc"
                }
                "테스트" -> {
                    terminalViewModel.showCommander(id, "항우")
                }
                else -> "?"
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when(it) {
                    is String -> terminalViewModel.updateMessage(id, it)
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTerminalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = LineAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.terminal_recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val currTime = System.currentTimeMillis()

        terminalViewModel.getCurrLines(currTime).observe(this) { lines ->
            lines.let {
                adapter.submitList(it)
            }
        }

        terminalViewModel.insert(Line(0, currTime, null, "welcome (${currTime})"))
        terminalViewModel.insert(Line(0, System.currentTimeMillis(), "", ""))

        registerReceiver(editLineReceiver, IntentFilter(ACTION_ADD_LINE))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(editLineReceiver)
    }


    private val editLineReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("Terminal Receive", intent.toString())
            when (intent?.action) {
                ACTION_ADD_LINE -> {
                    val command: String? = intent.extras?.getString(EXTRAS_ADD_LINE_COMMAND)
                    intent.extras?.getLong(EXTRAS_ADD_LINE_ID)?.let {
                        terminalViewModel.updateCommand(it, command)
                        cmdTask(it, command)
                    }



                    terminalViewModel.insert(Line(0, System.currentTimeMillis(), "", ""))
                }
            }
        }
    }

    private fun getJsonFromAsset(context: Context, fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use {
                it.readText()
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }


    private fun createOrUpdateData(clazz: Class<*>, tuple: JSONObject, gson: Gson) {
        try {
            val testObj = gson.fromJson(tuple.toString(), clazz)
            terminalViewModel.insertRokEntity(testObj, clazz.kotlin)
        } catch (e: Exception) {
            Log.e("test~", e.message.toString())
        }

        //Log.d("test~", "data entity created:${testObj}")


        /*
        realm.writeBlocking {
            copyToRealm(realmObject.newInstance(), UpdatePolicy.ALL)
        }
        tuple.keys().toObservable().map{ header ->
            val headerSplit:MutableList<String> =  header.split("_").toMutableList()
            var suffix = headerSplit[headerSplit.size-1].lowercase(Locale.getDefault())
            if(suffix == "json") {
                headerSplit.removeLast()
                suffix = headerSplit[headerSplit.size-1].lowercase(Locale.getDefault())
            }
            val realmPropertyName = headerSplit[0]
            realmObject.getField(headerSplit[0])
            when(headerSplit.size) {
                3-> {
                    val pClazz: Class<out RealmObject>? = searchRealmClazzByName(realm, headerSplit[1])
                    if( pClazz != null ) {
                        when(suffix) {
                            "ids" -> {
                                val idsJsonArray = tuple.getJSONArray(header)
                                val pObjRealmList = realmListOf(pClazz.kotlin)
                                IntStream.range(0, idsJsonArray.length())
                                    .mapToObj(idsJsonArray::getInt)
                                    .map{id-> {
                                        val pObjResults = realm.query(pClazz.kotlin, "id == $(0)", id).find()
                                        if( pObjResults.size > 0 ) {
                                            pObjRealmList.add(pObjResults.first().javaClass.asSubclass(RealmObject::class.java).kotlin)
                                        } else {
                                            val pObj: RealmObject = gson.fromJson("{\"id\":${id}}",pClazz)
                                            realm.writeBlocking {
                                                copyToRealm(pObj)
                                            }
                                            pObjRealmList.add(pObj.javaClass.asSubclass(RealmObject::class.java).kotlin)
                                        }

                                    }}
                                realmObject.getField(realmPropertyName).set(realmObject,pObjRealmList)
                            }
                            "id", "name" -> {
                                val value = tuple.get(header)
                                val pObjResults = realm.query(pClazz.kotlin, "${suffix} == $(0)", value).find()
                                if( pObjResults.size > 0 ) {
                                    realmObject.getField(realmPropertyName).set(realmObject,pObjResults.first())
                                } else {
                                    val pObj = gson.fromJson("{\"${suffix}\":${value}", pClazz)
                                    realm.writeBlocking {
                                        copyToRealm(pObj)
                                    }
                                    realmObject.getField(realmPropertyName).set(realmObject,pObj)
                                }
                            }
                        }
                    }
                } // 3
                2 -> {
                    when(suffix) {
                        "kor" -> {
                            
                        }
                    }
                } // 2
            }
            return@map realmObject.toString()
        }.subscribe{
            Log.d("linking", it)
        }

         */
    }

    private fun jsonToDatabase(jsonString: String): String {

        val gson = GsonBuilder()
            .setExclusionStrategies(object : ExclusionStrategy {
                override fun shouldSkipField(field: FieldAttributes?): Boolean {
                    if (field != null) {
                        return field.name.contains("_ignore")
                    }
                    return false
                }

                override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                    return false
                }
            }).registerTypeAdapter(object: TypeToken<Boolean>(){}.type, BooleanDeserializer()).create()

        val json = JSONObject(jsonString)
        if (json.has("Version")) {
            json.remove("Version")
        }



        json.keys().forEach { modelName ->
            searchDataClazzByTableName(modelName)?.run {
                val tableData: JSONArray = json[modelName] as JSONArray
                for (i in 0 until tableData.length()) {
                    val tuple: JSONObject = tableData.getJSONObject(i)
                    createOrUpdateData(this, tuple, gson)
                }
            }
        }
        return "read!"
    }

    private fun searchDataClazzByTableName(tableName: String): Class<*>? {
        val clazzName = tableName.split("_")
            .joinToString("") { it.replaceFirstChar { c: Char -> c.uppercase() } }

        return try {
            if (clazzName.length > 8 && clazzName.takeLast(8) == "CrossRef")
                Class.forName("com.rx.starfang.database.room.rok.cross_ref.${clazzName}")
            else
                Class.forName("com.rx.starfang.database.room.rok.source.${clazzName}")
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    class BooleanDeserializer : JsonDeserializer<Boolean> {

        companion object {
            @JvmStatic lateinit var TRUE_STRINGS : HashSet<String>
        }

        init {
            TRUE_STRINGS = HashSet(listOf("true", "1", "yes"))
        }
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Boolean {
            json?.run {
                return if(asJsonPrimitive.isBoolean)
                    asJsonPrimitive.asBoolean
                else if(asJsonPrimitive.isNumber)
                    asJsonPrimitive.asNumber.toInt() > 0
                else if(asJsonPrimitive.isString)
                    TRUE_STRINGS.contains(asJsonPrimitive.asString.lowercase())
                else
                    false
            }
            return false
        }

    }
}

/*
    class LanguagePackDeserializer: JsonDeserializer<LanguagePack> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LanguagePack {
            return LanguagePack()
        }

    }
     */