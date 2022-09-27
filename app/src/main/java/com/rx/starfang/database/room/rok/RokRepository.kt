package com.rx.starfang.database.room.rok

import android.util.Log
import androidx.annotation.WorkerThread
import com.rx.starfang.database.room.rok.entities.*
import com.rx.starfang.database.room.rok.pojo.CmdrWithRarity
import com.rx.starfang.database.room.rok.pojo.CmdrWithSkills
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
    suspend fun searchSkills(
        names: String,
        skillLevels: List<Int>?,
        digitBefore: Int?,
        digitsAfter: Int?
    ): List<String>? {
        //Log.d("rok_repo", skillLevels.toString())
        val skillPos: Int? =
            if (digitBefore is Int && digitBefore < 6 && digitBefore > 0) digitBefore
            else if (digitsAfter is Int && digitsAfter > 0 && digitsAfter < 6) digitsAfter
            else null


        var rarIdSet: MutableSet<Long>? = null
        val cmdrSet: MutableSet<Any> = mutableSetOf()
        for (name in names.split("\\s+".toRegex())) {
            if (name.length < 2) continue
            val rarity: Rarity? = (rokDaoMap[Rarity::class] as RarityDao).searchRarityByName(name)
            if (rarity != null) {
                if (rarIdSet == null)
                    rarIdSet = mutableSetOf()
                rarIdSet.add(rarity.id)
                continue
            }

            if (skillPos is Int) {
                cmdrSet.addAll((rokDaoMap[Commander::class] as CmdrDao).searchCmdrsByName(name))
            } else {
                cmdrSet.addAll(
                    (rokDaoMap[Commander::class] as CmdrDao).searchCmdrsWithSkillsByName(
                        name
                    )
                )
            }
        }

        if (cmdrSet.isEmpty())
            return null

        val infoList = mutableListOf<String>()
        cmdrSet.filter { entity ->
            when (entity) {
                is CmdrWithRarity -> rarIdSet?.run {
                    entity.cmdr.rarityId in rarIdSet
                } ?: true
                is CmdrWithSkills -> rarIdSet?.run {
                    entity.cmdr.rarityId in rarIdSet
                } ?: true
                else -> false
            }
        }.forEach { cmdrEntity ->
            if (skillPos == null && cmdrEntity is CmdrWithSkills) {
                cmdrEntity.run {
                    infoList.add(
                        StringBuilder().append(cmdrTitle(cmdr, rarity))
                            .append(skills?.run {
                                val skillSb = StringBuilder()
                                for (i in indices) {
                                    get(i).run {
                                        skillInfo(
                                            skill,
                                            enhancedSkill,
                                            enhancingSkill,
                                            skillLevels,
                                            notes
                                        )
                                            ?.run {
                                                skillSb.append("\r\n\r\n").append(toString())
                                            }
                                    }
                                }
                                skillSb.toString()
                            }).toString()
                    )
                }
            } else if (skillPos is Int && cmdrEntity is CmdrWithRarity) {
                (rokDaoMap[Skill::class] as SkillDao).searchOneOfSkillsByCmdrId(
                    cmdrEntity.cmdr.id,
                    skillPos
                )
                    ?.run {
                        infoList.add(
                            StringBuilder()
                                .append(cmdrTitle(cmdrEntity.cmdr, cmdrEntity.rarity))
                                .append(skillInfo(
                                    skill, enhancedSkill, enhancingSkill,
                                    skillLevels,
                                    notes
                                )?.run { "\r\n${toString()}" } ?: "")
                                .toString()
                        )

                    }
            }

        }




        return if (infoList.size > 0) infoList else null
    }

    @WorkerThread
    suspend fun searchEntities(names: String, skillLevels: List<Int>?): List<String>? {
        val resultSet = mutableSetOf<SearchPojo>()
        for (name in names.split("\\s+".toRegex())) {
            Log.d("rok_repo", name)
            if (name.length < 2) continue
            resultSet.addAll(rokSearchDao.search(name))
        }
        if (resultSet.isEmpty())
            return null


        val entityInfoList = mutableListOf<String>()
        val rarIdList: List<Long> =
            resultSet.filter { entity -> entity.type.lowercase() == "rar" }.map { it.id }
        val cmdrIdList: List<Long> =
            resultSet.filter { entity -> entity.type.lowercase() == "cmdr" && (rarIdList.isEmpty() || entity.propId in rarIdList) }
                .map { it.id }

        val eqptSlotIdList: List<Long>? = resultSet.filter {
                entity -> entity.type.lowercase() == "eqpt_slot"
        }.run {
            if(isEmpty() && cmdrIdList.isNotEmpty()) {
                cmdrIdList.forEach { cmdrId ->
                    cmdrInfo(
                        cmdrId,
                        skillLevels ?: listOf(5, 5, 5, 5)
                    )?.let { cmdrStr -> entityInfoList.add(cmdrStr) }
                }
            } else {
                map{it.id}.let{
                    //todo: talent, eqpt 연결
                    return@run it
                }
            }
            return@run null
        }


        resultSet.filter { entity ->
            entity.type.lowercase() == "eqpt"
                    && (eqptSlotIdList == null || entity.propId in eqptSlotIdList)
        }.run {
            if(isEmpty()) return@run null
            val itemSetMap = hashMapOf<Long, Int>()
            map { it.id }.forEach { eqptId ->
                eqptInfo(eqptId) { setId ->
                    itemSetMap[setId] = itemSetMap[setId]?.plus(1) ?: 1
                }?.let { eqptStr ->
                    entityInfoList.add(eqptStr)
                }
            }
            return@run if(itemSetMap.size > 0) itemSetMap else null
        }?.let{ itemSetMap ->
            eqptSetInfo(itemSetMap) { itemSetInfo ->
                entityInfoList.add(itemSetInfo)
            }
        }


        resultSet.filter { entity -> entity.type.lowercase() == "civ" }.map { it.id }
            .forEach { civId ->
                civInfo(civId)?.let { civStr -> entityInfoList.add(civStr) }
            }



        return entityInfoList
    }

    @WorkerThread
    suspend fun eqptInfo(id: Long, lambda: (Long) -> Unit): String? {
        (rokDaoMap[Equipment::class] as EqptDao).searchEqptById(id)?.run {
            eqptSet?.run { lambda(this.id) }
            return StringBuilder()
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
                .append(matls?.run {
                    val matlInfoList = mutableListOf<String>()
                    for (i in indices) {
                        matlInfoList.add(
                            "${get(i).type.name?.kor ?: "?"}${matlRefs?.run { if (i < size) get(i).matlCount else "?" }}"
                        )
                    }
                    "\r\n${
                        matlInfoList.joinToString(
                            separator = ", ",
                            prefix = "*"
                        )
                    }"
                })
                .append(eqptSet?.name?.run { "\r\n*$kor" } ?: "")
                .toString()
        }
        return null
    }

    @WorkerThread
    suspend fun civInfo(id: Long): String? {
        (rokDaoMap[Civilization::class] as CivDao).searchCivById(id)?.run {
            return StringBuilder()
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
                .append(startingCommander?.run { "*초기 사령관: ${this.name?.kor ?: "?"}" } ?: "")
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
        return null
    }

    @WorkerThread
    suspend fun eqptSetInfo(itemSetMap: HashMap<Long, Int>, lambda: (String) -> Unit) {
        itemSetMap.entries.forEach { (id, count) ->
            (rokDaoMap[EquipmentSet::class] as EqptSetDao).searchEqptSetById(id)?.run {
                lambda(
                    StringBuilder()
                        .append(eqptSet.name?.kor ?: "?")
                        .append("$count 개")
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
                        }).toString()
                )
            }
        }
    }


    @WorkerThread
    suspend fun cmdrInfo(id: Long, skillLevels: List<Int>): String? {
        (rokDaoMap[Commander::class] as CmdrDao).searchCmdrWithAllInfoById(id)?.run {
            val sb = StringBuilder()
            sb.append(cmdr.nickname?.kor ?: "?")
                .append("\r\n")
                .append(cmdrTitle(cmdr, rarity))
                .append(talents?.run {
                    "\r\n${
                        joinToString(
                            separator = ", ",
                            prefix = "*",
                            transform = { it.name?.kor ?: "?" })
                    }"
                } ?: "")
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
                    for (i in indices) {
                        get(i).run {
                            skillInfo(skill, enhancedSkill, enhancingSkill, skillLevels, null)
                                ?.run {
                                    skillSb.append("\r\n\r\n*").append(toString())
                                }
                        }
                    }
                    skillSb.toString()
                })
            return sb.toString()
        }
        return null
    }

    private fun cmdrTitle(cmdr: Commander, rarity: Rarity?): String {
        return "${cmdr.name?.kor ?: "?"}${if (cmdr.isPrime == true) "℗" else ""} ${rarity?.name?.run { "($kor)" } ?: ""}"
    }


    private fun skillInfo(
        skill: Skill,
        enhancedSkill: Skill?,
        enhancingSkill: Skill?,
        skillLevels: List<Int>?,
        notes: List<SkillNote>?
    ): StringBuilder? {
        val skillLevel: Int? =
            if (skillLevels != null && skillLevels.size > skill.position - 1) skillLevels[skill.position - 1] else null
        if (skillLevel == 0)
            return null
        val maxSkill: Boolean = skillLevels?.run { filter { a -> a == 5 }.size == size } ?: false

        if (skill.position == 5 && skillLevels != null && !maxSkill)
            return null

        val enhanced: Boolean = maxSkill && enhancingSkill != null
        val messageFormValues: List<Any>? = if (!enhanced)
            skillLevel?.run {
                val maxCoefficientList = mutableListOf<Double>()
                skill.coefficient?.forEach {
                    maxCoefficientList.add(it[max(0, min(skillLevel, it.size) - 1)])
                }
                return@run maxCoefficientList
            } ?: skill.coefficient else null


        return StringBuilder().append("${if (skill.position == 5) "각성" else skill.position} 스킬: ${skill.name?.kor ?: "?"}")
            .append(skillLevel?.run { " (lv.$skillLevel)" } ?: "")
            .append("\r\n")
            .append(enhancedSkill?.run { "->강화: ${this.name?.kor ?: "?"}(${this.position} 스킬)\r\n" }
                ?: "")
            .append(if (skill.isActive == true) "->액티브 (분노 ${skill.rage} 포인트)\r\n" else "")
            .append(enhancingSkill?.run {
                if (!enhanced)
                    "->${this.name?.kor ?: "?"}(${this.position} 스킬)습득 시 강화\r\n"
                else
                    "스킬 강화\r\n"
            } ?: "")
            .append(if (enhanced) enhancingSkill!!.description?.kor ?: "??" else
                skill.description?.run {
                    try {
                        val valuesVararg = messageFormValues?.toTypedArray() ?: arrayOf()
                        MessageFormat.format(kor, *valuesVararg)
                    } catch (e: IllegalArgumentException) {
                        e.toString()
                    }
                } ?: "")
            .append(notes?.run {
                val noteSb = StringBuilder()
                forEach { note ->
                    noteSb.append(note.description?.run {
                        "\r\n -${note.name?.kor ?: "?"}: ${kor ?: "?"}"
                    })
                }
                noteSb.toString()
            } ?: "")
    }

}