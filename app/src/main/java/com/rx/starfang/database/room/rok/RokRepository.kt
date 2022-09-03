package com.rx.starfang.database.room.rok

import android.util.Log
import androidx.annotation.WorkerThread
import com.rx.starfang.database.room.rok.entities.*
import com.rx.starfang.database.room.rok.pojo.SearchPojo
import java.lang.IllegalArgumentException
import java.text.MessageFormat
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass
import kotlin.text.StringBuilder

class RokRepository(
    private val rokDaoMap: HashMap<Any, Any>,
    private val rokSearchDao: RokSearchDao
) {

    @WorkerThread
    suspend fun insertEntity(entity: Any, clazz: KClass<*>) {

        when (val dao = rokDaoMap[clazz]) {
            is RokBaseDao<*> -> {
                //Log.d("test", "try to insert $entity to $clazz")
                @Suppress("UNCHECKED_CAST")
                (dao as RokBaseDao<Any>).insert(entity)
            }
            else -> {
                Log.d("rok_repo", "cannot insert $entity to $clazz")
            }
        }
    }

    @WorkerThread
    suspend fun searchEntities(names: String, skillLevels: List<Int>?): List<String?> {

        val entityInfoList = mutableListOf<String?>()
        for (name in names.split("\\s+".toRegex())) {
            Log.d("rok_repo", name)
            if(name.length<2) continue
            val entities: List<SearchPojo> = rokSearchDao.search(name)

            val itemSetMap = hashMapOf<Long, Int>()
            for (entity in entities) {
                Log.d("rok_repo", "${entity.name} type of ${entity.type}")
                entityInfoList.add(when (entity.type.lowercase()) {
                    "civ" ->
                        (rokDaoMap[Civilization::class] as CivDao).searchCivById(entity.id)?.run {
                            StringBuilder()
                                .append(civ.comment?.kor ?: "?").append(". ")
                                .append(civ.name?.kor ?: "?")
                                .append(attrs?.run {
                                    val attrSb = StringBuilder()
                                    for (i in indices) {
                                        attrSb.append("\r\n - ").append(
                                            get(i).form?.kor?.let { form ->
                                                attrRefs?.get(i)?.attrValues?.run {
                                                    try {
                                                        MessageFormat.format(
                                                            form,
                                                            *toTypedArray()
                                                        )
                                                    } catch (e: IllegalArgumentException) {
                                                        e.toString()
                                                    }
                                                }
                                            })
                                    }
                                    attrSb.toString()
                                })
                                .append("\r\n")
                                .append(startingCommander?.run{ "*초기 사령관: ${this.name?.kor ?: "?"}"} ?: "")
                                .append(specialUnits?.run {
                                    val unitSb = StringBuilder()
                                    forEach { specialUnit ->
                                        val statMap: HashMap<String, Int?> = hashMapOf(
                                            "공" to specialUnit.unit.attack,
                                            "방" to specialUnit.unit.defense,
                                            "생" to specialUnit.unit.health
                                        )
                                        val statPlusMap = hashMapOf<String, Int>()
                                        specialUnit.typedBaseUnit?.run {
                                            statPlusMap["공"] =
                                                statMap["공"]?.run { minus(baseUnit.attack) } ?: 0
                                            statPlusMap["방"] =
                                                statMap["방"]?.run { minus(baseUnit.defense) } ?: 0
                                            statPlusMap["생"] =
                                                statMap["생"]?.run { minus(baseUnit.health) } ?: 0
                                        }
                                        unitSb.append("\r\n*T")
                                            .append(
                                                specialUnit.typedBaseUnit?.baseUnit?.tier ?: "?"
                                            )
                                            .append(" ")
                                            .append(
                                                specialUnit.typedBaseUnit?.unitType?.name?.kor
                                                    ?: "?"
                                            ).append(": ")
                                            .append(specialUnit.unit.name?.kor).append("\r\n ->")
                                            .append(statMap.entries.run {
                                                val statSb = StringBuilder()
                                                forEach { entry ->
                                                    statSb.append(entry.key)
                                                        .append(entry.value)
                                                        .append(statPlusMap[entry.key]?.run {
                                                            if (this == 0) "" else {
                                                                if (this > 0) "(+$this)" else "($this)"
                                                            }
                                                        })
                                                        .append(" ")
                                                }
                                                statSb.toString()
                                            })
                                    }
                                    unitSb.toString()
                                })
                                .toString()
                        }
                    "cmdr" -> cmdrInfo(entity.id, skillLevels ?: listOf(5,5,5,5))?.run { this }
                    "eqpt" ->
                        (rokDaoMap[Equipment::class] as EqptDao).searchEqptById(entity.id)?.run {
                            eqptSet?.run {
                                itemSetMap[id] = itemSetMap[id]?.plus(1) ?: 1
                            }
                            StringBuilder()
                                .append(rarity?.run { "${this.name?.kor} " } ?: "")
                                .append(slot?.name?.kor ?: "")
                                .append("\r\n")
                                .append(eqpt.name?.kor ?: "?")
                                .append(attrs?.run {
                                    val attrSb = StringBuilder()
                                    for (i in indices) {
                                        attrSb.append("\r\n - ").append(
                                            get(i).form?.kor?.let { form ->
                                                attrRefs?.get(i)?.attrValues?.run {
                                                    try {
                                                        MessageFormat.format(form, *toTypedArray())
                                                    } catch (e: IllegalArgumentException) {
                                                        e.toString()
                                                    }
                                                }
                                            })
                                    }
                                    attrSb.toString()
                                })
                                .append("\r\n$matls")
                                .append(eqptSet?.name?.run { "\r\n*$kor" } ?: "")
                                .toString()


                        }
                    else -> null
                }
                )
            }
            itemSetMap.entries.forEach { (id, count) ->
                eqptSetInfo(id, count)?.run {
                    entityInfoList.add(this)
                }
            }
        }
        return entityInfoList
    }

    @WorkerThread
    suspend fun eqptSetInfo(id: Long, count: Int): String? {
        (rokDaoMap[EquipmentSet::class] as EqptSetDao).searchEqptSetById(id)?.run {
            val eqptSetSb = StringBuilder()
            eqptSetSb.append(eqptSet.name?.kor ?: "?")
                .append(attrs?.run {
                    val attrSb = StringBuilder()
                    Log.d("rok_repo", attrs.toString())
                    for (i in indices) {
                        val setCount: Int = attrRefs?.get(i)?.setCount ?: 0
                        attrSb.append("\r\n -")
                            .append(if (setCount > 0) "${setCount}개: " else " ")
                            .append(this[i].form?.kor?.let { form ->
                                attrRefs?.get(i)?.attrValues?.run {
                                    try {
                                        MessageFormat.format(form, *toTypedArray())
                                    } catch (e: IllegalArgumentException) {
                                        e.toString()
                                    }
                                }
                            })
                            .append(if (count > setCount) " [O]" else " [X]")
                    }
                    attrSb.toString()
                })
            return eqptSetSb.toString()
        }
        return null
    }


    @WorkerThread
    suspend fun cmdrInfo(id: Long, skillLevels: List<Int>): String? {
        (rokDaoMap[Commander::class] as CmdrDao).searchCmdrById(id)?.run {
            val sb = StringBuilder()
            sb.append(rarity?.run { "${this.name?.kor} " } ?: "").append("사령관\r\n")
                .append(cmdr.nickname?.kor ?: "?").append(". ")
                .append(cmdr.name?.kor ?: "?")
                .append(if (cmdr.isPrime == true) "℗" else "")
                .append(talents?.run {
                     "\r\n${joinToString(separator = ", ", prefix = "*", transform = { it.name?.kor ?: "?" })}"
                } ?: "")
                //.append(cmdrInfo.civ?.run { "\r\n문명: ${this.name?.kor}" })
                .append(relic?.run {
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
                } ?: "")
                .append(enhancingSkills?.run {
                    val skillSb = StringBuilder()
                    for( i in indices ) {
                        val enhancingSkill = get(i)
                        enhancingSkill.skill.run {
                            val maxCoefficientList = mutableListOf<Double>()
                            var skillLevel: Int? = null
                            coefficient?.run {
                                skillLevel = if(skillLevels.size > i) skillLevels[i] else null
                                forEach {
                                    maxCoefficientList.add(it[max(0,min(skillLevel ?: 5, it.size) - 1)])
                                }

                            }
                            skillSb.append("\r\n\r\n*${if (position == 5) "각성" else position} 스킬: ${name?.kor ?: "?"}")
                                .append(skillLevel?.run {" (lv.$skillLevel)"} ?: "").append("\r\n")
                                .append(enhancingSkill.enhancedSkill?.run { "->강화: ${this.name?.kor ?: "?"}(${this.position} 스킬)\r\n"} ?: "")
                                .append(if(isActive == true) "->액티브 (분노 $rage 포인트)\r\n" else "")
                                .append(enhancingSkill.enhancingSkill?.run { "->${this.name?.kor ?: "?"}(${this.position} 스킬)습득 시 강화\r\n" } ?: "")
                                .append(description?.kor?.let { desc ->
                                    //Log.d("test", maxLevelCoefficientList.toString())
                                    try {
                                        MessageFormat.format(
                                            desc,
                                            *maxCoefficientList.toTypedArray()
                                        )
                                    } catch (e: IllegalArgumentException) {
                                        e.toString()
                                    }
                                })
                        }

                    }
                    skillSb.toString()
                })
            return sb.toString()
        }
        return null
    }

}