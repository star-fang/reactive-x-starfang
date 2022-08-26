package com.rx.starfang.database.room.rok

import android.util.Log
import androidx.annotation.WorkerThread
import com.rx.starfang.database.room.rok.pojo.CmdrAllInclusive
import com.rx.starfang.database.room.rok.pojo.EqptAllInclusive
import com.rx.starfang.database.room.rok.pojo.SearchPojo
import com.rx.starfang.database.room.rok.source.CmdrDao
import com.rx.starfang.database.room.rok.source.Commander
import com.rx.starfang.database.room.rok.source.EqptDao
import com.rx.starfang.database.room.rok.source.Equipment
import java.lang.IllegalArgumentException
import java.text.MessageFormat
import kotlin.reflect.KClass
import kotlin.text.StringBuilder

class RokRepository(private val rokDaoMap: HashMap<Any, Any>, private val rokSearchDao: RokSearchDao) {

    @WorkerThread
    suspend fun insertEntity(entity: Any, clazz: KClass<*>) {

        when (val dao = rokDaoMap[clazz]) {
            is RokBaseDao<*> -> {
                //Log.d("test", "try to insert $entity to $clazz")
                @Suppress("UNCHECKED_CAST")
                (dao as RokBaseDao<Any>).insert(entity)
            }
            else -> {
                Log.d("test~", "cannot insert $entity to $clazz")
            }
        }
    }

    @WorkerThread
    suspend fun searchEntities(name: String): List<String>? {
        val entities: List<SearchPojo> = rokSearchDao.search(name)
        for( entity in entities) {
            Log.d("test", "${entity.name} type of ${entity.type}")
        }
        return null
    }

    @WorkerThread
    suspend fun showEquipment(name: String): List<String>? {
        val eqpts: List<EqptAllInclusive> =
            (rokDaoMap[Equipment::class] as EqptDao).searchEqptByName(name)

        return if(eqpts.isEmpty()) null else {
            val eqptInfoList = mutableListOf<String>()
            for( eqptInfo in eqpts) {
                val eqptSb = StringBuilder()
                eqptSb.append(eqptInfo.rarity?.run{"${this.name?.kor} " } ?: "")
                    .append(eqptInfo.slot?.name?.kor ?: "")
                    .append("\r\n")
                    .append(eqptInfo.eqpt.name?.kor ?: "?")
                    .append(eqptInfo.attrs?.run{
                        val attrSb = StringBuilder()
                        for(i in indices) {
                            attrSb.append("\r\n - ").append(
                                this[i].form?.kor?.let{ form ->
                                    eqptInfo.attrRefs?.get(i)?.attrValues?.run {
                                        try {
                                            MessageFormat.format(form, *toTypedArray())
                                        } catch( e: IllegalArgumentException) {
                                            e.toString()
                                        }
                                    }
                                })
                        }
                        attrSb.toString()
                    })
            }
            eqptInfoList
        }
    }

    @WorkerThread
    suspend fun showCommander(name: String): List<String> {
        val cmdrInfoList = mutableListOf<String>()
        val cmdrs: List<CmdrAllInclusive> =
            (rokDaoMap[Commander::class] as CmdrDao).searchCmdrsByName(name)

        for (cmdrInfo in cmdrs) {
            val sb = StringBuilder()
            sb.append(cmdrInfo.rarity?.run { "${this.name?.kor} " } ?: "").append("사령관\r\n")
                .append(cmdrInfo.cmdr.nickname?.kor ?: "?").append(". ")
                .append(cmdrInfo.cmdr.name?.kor ?: "?")
                .append(if(cmdrInfo.cmdr.isPrime == true) "℗" else "")
                .append(cmdrInfo.talents?.run{
                    val talentSb = StringBuilder()
                    forEach {talentSb.append(it.name?.kor).append(" ")}
                    "\r\n${talentSb.toString().trim()}"
                })
                //.append(cmdrInfo.civ?.run { "\r\n문명: ${this.name?.kor}" })
                .append(cmdrInfo.relic?.run {
                    val relicSb = StringBuilder()
                    for (i in attrs.indices) {
                        relicSb.append("\r\n - ").append(attrs[i].form?.kor?.let {
                            try {
                                attrRefs[i].attrValues?.run {
                                    MessageFormat.format(it, *toTypedArray())
                                }
                            } catch (e: IllegalArgumentException) {
                                e.toString()
                            }
                        })
                    }
                    "\r\n*${relic.name?.kor ?: ""}$relicSb"
                    //relicSb.toString()
                })
                .append(cmdrInfo.skills?.run {
                    val skillSb = StringBuilder()
                    forEach { skill ->
                        val maxCoefficientList = mutableListOf<Double>()
                        skill.coefficient?.run {
                            forEach {
                                maxCoefficientList.add(it.last())
                            }

                        }
                        skillSb.append("\r\n\r\n${if (skill.position == 5) "각성" else skill.position} 스킬: ${skill.name?.kor}\r\n")
                            .append(skill.description?.kor?.let { desc ->
                                //Log.d("test", maxLevelCoefficientList.toString())
                                try {
                                    MessageFormat.format(desc, *maxCoefficientList.toTypedArray())
                                } catch (e: IllegalArgumentException) {
                                    e.toString()
                                }
                            })
                    }
                    skillSb.toString()
                })
            cmdrInfoList.add(sb.toString())
        }


        return cmdrInfoList
    }
}