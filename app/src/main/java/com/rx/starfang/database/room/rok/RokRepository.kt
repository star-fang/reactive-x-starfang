package com.rx.starfang.database.room.rok

import android.util.Log
import androidx.annotation.WorkerThread
import com.rx.starfang.database.room.rok.source.CmdrDao
import com.rx.starfang.database.room.rok.source.Commander
import kotlin.reflect.KClass
import kotlin.text.StringBuilder

class RokRepository(private val rokDaoMap: HashMap<Any, Any>) {

    @WorkerThread
    suspend fun insertEntity(entity: Any, clazz: KClass<*>) {

        when (val dao = rokDaoMap[clazz]) {
            is RokBaseDao<*> -> {
                Log.d("test", "try to insert $entity to $clazz")
                @Suppress("UNCHECKED_CAST")
                (dao as RokBaseDao<Any>).insert(entity)
            }
            else -> {
                Log.d("test~", "cannot insert $entity to $clazz")
            }
        }
    }

    @WorkerThread
    suspend fun showCommander(name: String): String {
        val resultBuilder = StringBuilder()
        (rokDaoMap[Commander::class] as CmdrDao).searchCmdrsByName(name).forEach {
            Log.d("test", it.toString())
            resultBuilder.append(it.toString())
        }
        return resultBuilder.toString()
    }
}