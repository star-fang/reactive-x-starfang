package com.rx.starfang.database.room.rok

import androidx.annotation.WorkerThread
import com.rx.starfang.database.room.rok.cross_ref.CivAttrCrossRef
import com.rx.starfang.database.room.rok.cross_ref.CivAttrXRefDao
import com.rx.starfang.database.room.rok.source.CmdrDao
import com.rx.starfang.database.room.rok.source.Commander
import kotlin.reflect.KClass

class RokRepository( private val cmdrDao: CmdrDao) {

    @WorkerThread
    suspend fun <T: Any> insertEntity(clazz: KClass<T>, entity: Any) {
        when(clazz) {
           Commander::class -> cmdrDao.insert(entity as Commander)
            CivAttrCrossRef::class -> {}//CivAttrXRefDao()
        }
    }
}