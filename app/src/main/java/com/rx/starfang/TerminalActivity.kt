package com.rx.starfang

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.rx.starfang.databinding.ActivityTerminalBinding
import com.rx.starfang.ui.list.adapter.LineAdapter
import com.rx.starfang.ui.model.TerminalViewModel
import com.rx.starfang.ui.model.TerminalViewModelFactory
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.reflect.Type
import kotlin.collections.HashSet
import kotlin.coroutines.CoroutineContext

class TerminalActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityTerminalBinding

    private val modelParsingGson = GsonBuilder().setLenient()
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
        }).registerTypeAdapter(object : TypeToken<Boolean>() {}.type, BooleanDeserializer())
        .create()

    private val terminalViewModel: TerminalViewModel by viewModels {
        (application as RxStarfangApp).run {
            TerminalViewModelFactory(
                memoRepository, terminalRepository, rokRepository
            )
        }

    }

    companion object {
        val TAG: String = TerminalActivity::class.java.simpleName
        const val RC_MEMO = 8001
        const val RC_PERMISSION = 7001
        val MEMO_URI: Uri = Uri.Builder()
            .scheme("content")
            .authority("com.starfang")
            .appendPath("memo")
            .build()
        val MEMO_PROJ = arrayOf("name", "when", "content", "sendCat", "forumId")

        const val ACTION_ADD_LINE = "ACTION_ADD_LINE"
        const val EXTRAS_ADD_LINE_ID = "EXTRAS_ADD_LINE_ID"
        const val EXTRAS_ADD_LINE_COMMAND = "EXTRAS_ADD_LINE_COMMAND"
        const val CMD_CONNECTION = "/connect"
        const val CMD_TALK = "/talk"
        const val CMD_MEMO = "/memo"

        //const val PERMISSION_RES = android.Manifest.permission.READ_EXTERNAL_STORAGE
        const val PERMISSION_DYNAMIC_STARFANG_R_DB = "com.starfang.READ_DATABASE"
        //const val PERMISSION_DYNAMIC_STARFANG_W_DB = "com.starfang.WRITE_DATABASE"
    }

    private fun checkPermissionAndPerformTriggeredAction(
        permission: String?,
        requestCode: Int,
        trigger: () -> Unit
    ) {
        if (permission is String) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    requestCode
                )
            } else {
                trigger()
            }
        } else {
            trigger()
        }

    }


    private suspend fun getMemoList() {
        val messageLineIdLiveData = MutableLiveData<Long>()
        val messageLiveData = MutableLiveData("Sync memo from $MEMO_URI")
        val messageObserver = Observer<String> { message ->
            val lineIdObserver = Observer<Long> { lineId ->
                terminalViewModel.updateMessage(lineId, message)
            }
            messageLineIdLiveData.observe(this@TerminalActivity, lineIdObserver)
        }
        terminalViewModel.insertChangingMessageLine(
            this@TerminalActivity, messageLineIdLiveData,
            messageLiveData, messageObserver
        )

        val cursor = contentResolver.query(
            MEMO_URI,
            MEMO_PROJ,
            "name NOT IN",
            terminalViewModel.allMemoNames.value?.toTypedArray(),
            null
        )
        if (cursor == null) {
            messageLiveData.postValue("sync failure: Cursor does not exist")
        } else {
            cursor.run {
                if (count > 0) {
                    val progressLineIdLiveData = MutableLiveData<Long>()
                    val progressLiveData = MutableLiveData<String>()
                    val progressObserver = Observer<String> { message ->
                        progressLineIdLiveData.value?.let { lineId ->
                            terminalViewModel.updateMessage(lineId, message)
                        }
                    }
                    terminalViewModel.insertChangingMessageLine(
                        this@TerminalActivity,
                        progressLineIdLiveData, progressLiveData, progressObserver
                    )
                    var cursorCount = 0
                    var successCount = 0

                    while (moveToNext()) {
                        try {
                            var index = getColumnIndex(MEMO_PROJ[0])
                            val name = getString(index)
                            index = getColumnIndex(MEMO_PROJ[1])
                            val createTime = getLong(index)
                            index = getColumnIndex(MEMO_PROJ[2])
                            val content = getString(index)
                            index = getColumnIndex(MEMO_PROJ[3])
                            val creator = getStringOrNull(index)
                            //index = getColumnIndex(MEMO_PROJ[4])
                            //val forumId = cursor.getLong(index)

                            Log.d(TAG, "memo $name : $content ")
                            terminalViewModel.insertMemo(
                                name,
                                content,
                                creator ?: "unknown",
                                createTime
                            )
                            ++successCount
                        } catch (_: Exception) {
                        }

                        progressLiveData.postValue(
                            "${((++cursorCount).toDouble() / count.toDouble()) * 100.0}%"
                        )
                        delay(1)
                    }

                    delay(1000)

                    progressLiveData.postValue(
                        "$successCount memos synchronized (${if (cursorCount > successCount) "${cursorCount - successCount} error(s) occurred" else "no error"})"
                    )
                    progressLiveData.removeObserver(progressObserver)
                } else {
                    messageLiveData.postValue("All memos are up-to-date")
                }
                close()
            }
        }

        //messageLiveData.removeObserver(messageObserver)

        terminalViewModel.addCommandLine()


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_MEMO && permissions[0] == PERMISSION_DYNAMIC_STARFANG_R_DB) {
            launch {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMemoList()
                } else {
                    terminalViewModel.insert(null, "permission denied(read com.starfang)")
                    terminalViewModel.addCommandLine()
                    Log.d(TAG, "read dynamic starfang permission denied")
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun cmdTask(command: String) {
        when (command) {
            CMD_MEMO ->
                checkPermissionAndPerformTriggeredAction(
                    PERMISSION_DYNAMIC_STARFANG_R_DB, RC_MEMO
                ) {
                    launch {
                        getMemoList()
                    }
                }
            CMD_CONNECTION -> {
                checkPermissionAndPerformTriggeredAction(null, RC_PERMISSION) {
                    launch {
                        readJsonFromFile()
                        //terminalViewModel.updateMessage(lineId, "abc")
                        terminalViewModel.addCommandLine()
                    }
                }


            }
            CMD_TALK -> {
                startActivity(Intent(this@TerminalActivity, TalkActivity::class.java))
                terminalViewModel.addCommandLine()
            }
            else -> {
                terminalViewModel.addCommandLine()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        binding = ActivityTerminalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = LineAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.terminal_recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        val currTime = System.currentTimeMillis()

        terminalViewModel.getCurrLines(currTime).observe(this) { lines ->
            adapter.submitList(lines)
        }

        terminalViewModel.insert(null, "welcome ($currTime)", currTime)
        terminalViewModel.insert("", "")

        registerReceiver(editLineReceiver, IntentFilter(ACTION_ADD_LINE))
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        unregisterReceiver(editLineReceiver)
    }

    private val editLineReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("Terminal Receive", intent.toString())
            when (intent?.action) {
                ACTION_ADD_LINE -> {
                    val command: String? = intent.extras?.getString(EXTRAS_ADD_LINE_COMMAND)
                    val lineId: Long? = intent.extras?.getLong(EXTRAS_ADD_LINE_ID)
                    if (command != null && lineId != null) {
                        terminalViewModel.updateCommand(lineId, command)
                        cmdTask(command)
                    }
                }
            }
        }
    }


    private suspend fun readJsonFromFile(
        fileName: String = "RoK_FangDB.json"
    ) {

        val lineIdLiveData = MutableLiveData<Long>()
        val changingMessageLiveData = MutableLiveData("")
        val changingMessageObserver = Observer<String> { message ->
            lineIdLiveData.observe(
                this@TerminalActivity
            ) { lineId ->
                terminalViewModel.updateMessage(lineId, message)
            }
        }
        try {

            terminalViewModel.insertChangingMessageLine(
                this@TerminalActivity,
                lineIdLiveData,
                changingMessageLiveData,
                changingMessageObserver
            )
            val jsonReader = JsonReader(
                InputStreamReader(assets.open(fileName))
            ).apply {
                isLenient = true
            }

            when (jsonReader.peek()) {
                JsonToken.BEGIN_ARRAY -> {
                    jsonReader.beginArray()
                    //todo: arr
                    jsonReader.endArray()
                }
                JsonToken.BEGIN_OBJECT -> {
                    jsonReader.beginObject()

                    while (jsonReader.hasNext()) {
                        if (jsonReader.peek() == JsonToken.NAME) {
                            val modelName: String = jsonReader.nextName()
                            if (modelName.lowercase() == "version" && jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                                jsonReader.beginObject()
                                while (jsonReader.hasNext()) {
                                    if (jsonReader.peek() == JsonToken.NAME
                                        && jsonReader.nextName().lowercase() == "value"
                                        && jsonReader.peek() == JsonToken.STRING
                                    ) {
                                        changingMessageLiveData.postValue("Sync DB version:${jsonReader.nextString()}")
                                    } else {
                                        jsonReader.skipValue()
                                    }
                                }
                                jsonReader.endObject()
                            } else if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {

                                val modelClazz: Class<*>? = searchDataClazzByTableName(modelName)

                                if (modelClazz != null) {

                                    val progressLineIdLiveData = MutableLiveData<Long>()
                                    val progressLiveData = MutableLiveData("")
                                    delay(5)
                                    val progressObserver = Observer<String> { message ->
                                        progressLineIdLiveData.observe(
                                            this@TerminalActivity
                                        ) { lineId ->
                                            terminalViewModel.updateMessage(lineId, message)
                                        }
                                    }

                                    terminalViewModel.insertChangingMessageLine(
                                        this@TerminalActivity,
                                        progressLineIdLiveData, progressLiveData, progressObserver
                                    )
                                    jsonReader.beginArray()
                                    var progress = 0
                                    while (jsonReader.hasNext()) {
                                        parseTuple(modelParsingGson, modelClazz, jsonReader)
                                        progressLiveData.postValue("${modelClazz.simpleName}: ${++progress}")
                                        delay(1)
                                    }
                                    jsonReader.endArray()
                                    progressLiveData.removeObserver(progressObserver)
                                } else {
                                    jsonReader.skipValue()
                                }
                            } else {
                                jsonReader.skipValue()
                            }
                        }
                    }
                    jsonReader.endObject()
                }
                else ->
                    //todo : error message
                    return
            }

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        changingMessageLiveData.removeObserver(changingMessageObserver)

        /*
        return try {
            context.assets.open(fileName)
                .bufferedReader().use {
                    it.readText()
                }*/

    }

    /*
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
    */

    @Throws(IOException::class)
    private fun parseElement(reader: JsonReader): Any? {

        return when (reader.peek()) {
            JsonToken.STRING ->
                Gson().toJson(reader.nextString())
            JsonToken.BOOLEAN ->
                reader.nextBoolean()
            JsonToken.NUMBER ->
                reader.nextDouble()
            JsonToken.BEGIN_OBJECT -> {
                val objMap = mutableMapOf<String, Any>()
                reader.beginObject()
                while (reader.hasNext()) {
                    if (reader.peek() == JsonToken.NAME) {
                        val key = reader.nextName()
                        parseElement(reader)?.let { value ->
                            objMap[key] = value
                        }
                    } else {
                        reader.skipValue()
                    }
                }
                reader.endObject()
                objMap
            }
            JsonToken.BEGIN_ARRAY -> {
                val arrList = mutableListOf<Any?>()
                reader.beginArray()
                while (reader.hasNext()) {
                    arrList.add(parseElement(reader))
                }
                reader.endArray()
                arrList
            }
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            else -> {
                reader.skipValue()
                null
            }
        }

    }


    @Throws(IOException::class, JsonSyntaxException::class)
    private fun parseTuple(gson: Gson, modelClazz: Class<*>, reader: JsonReader) {
        val elementStr = parseElement(reader).toString().trim()
        Log.d(TAG, elementStr)
        val testObj = gson.fromJson(elementStr, modelClazz)

        terminalViewModel.insertRokEntity(testObj, modelClazz.kotlin)


    }

    private fun searchDataClazzByTableName(tableName: String): Class<*>? {
        val clazzName = tableName.split("_")
            .joinToString("") { it.replaceFirstChar { c: Char -> c.uppercase() } }

        return try {
            if (clazzName.length > 8 && clazzName.takeLast(8) == "CrossRef")
                Class.forName("com.rx.starfang.database.room.rok.cross_ref.${clazzName}")
            else
                Class.forName("com.rx.starfang.database.room.rok.entities.${clazzName}")
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    class BooleanDeserializer : JsonDeserializer<Boolean> {

        companion object {
            @JvmStatic
            lateinit var TRUE_STRINGS: HashSet<String>
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
                return if (asJsonPrimitive.isBoolean)
                    asJsonPrimitive.asBoolean
                else if (asJsonPrimitive.isNumber)
                    asJsonPrimitive.asNumber.toInt() > 0
                else if (asJsonPrimitive.isString)
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