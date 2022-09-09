package com.rx.starfang.nlp

import android.util.Log
import com.rx.starfang.database.room.rok.RokRepository
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.math.max

class RokLambda {
    enum class LangNum {
        KOR, ENG
    }


    enum class CmdNum {
        CMDR, SKILL, ATTR, CIV, EQPT, RELIC, CALC
    }

    companion object {

        private const val debugTag = "rok_lambda"

        private const val catCommand = "냥"
        private const val engCommand = "냥"

        val alias = HashMap<String, String>(
            mapOf(
                "기마병" to "기병"
            )
        )

        private val cmdKor3HashMap = HashMap<String, CmdNum>(
            mapOf(
                "사령관" to CmdNum.CMDR,
                "박물관" to CmdNum.RELIC
            )
        )


        private val cmdKor2HashMap = HashMap<String, CmdNum>(
            mapOf(
                //"장군" to CmdNum.CMDR,
                "스킬" to CmdNum.SKILL,
                "특성" to CmdNum.ATTR,
                "문명" to CmdNum.CIV,
                "장비" to CmdNum.EQPT,
                "계산" to CmdNum.CALC
            )
        )

        private val cmdMap = HashMap<Int, HashMap<String, CmdNum>>(
            mapOf(
                2 to cmdKor2HashMap,
                3 to cmdKor3HashMap
            )
        )

        private val cmdEngHashMap = HashMap<String, CmdNum>(
            mapOf(
                "cmdr" to CmdNum.CMDR,
                "skill" to CmdNum.SKILL,
                "attr" to CmdNum.ATTR,
                "civ" to CmdNum.CIV,
                "eqpt" to CmdNum.EQPT,
                "calc" to CmdNum.CALC
            )
        )

        fun preProc(contentText: String): String? {
            val textTrimmed = contentText.trim()
            if(textTrimmed.length > catCommand.length && textTrimmed.substring(textTrimmed.length- catCommand.length) == catCommand)
                return textTrimmed.substring(0, textTrimmed.length- catCommand.length).trim()
            return null
        }

        suspend fun process(content: String, sendCat: String, rokRepository: RokRepository): List<String?>?{
            var req = content
            val langNum: LangNum =
                if(content.length > engCommand.length && content.substring(content.length - engCommand.length) == engCommand) {
                    req = content.substring(0, content.length - engCommand.length)
                    LangNum.ENG
                } else LangNum.KOR

            var cmdNum: CmdNum? = null
            cmdMap.keys.forEach { keyLen ->
                val pivotIndex = max(0, req.length - keyLen)
                val key = req.substring(pivotIndex)
                cmdMap[keyLen]?.get(key)?.run {
                    cmdNum = this
                    req = req.substring(0, pivotIndex).trim()
                    return@forEach
                }
            }

            when(cmdNum) {
                CmdNum.CIV -> {}
                else -> {
                    var skillLevels: MutableList<Int>? = null
                    val skillLevelPattern = Pattern.compile("[0-9]{4}")
                    val skillLevelMatcher = skillLevelPattern.matcher(req)
                    if(skillLevelMatcher.find()) {
                        skillLevels = mutableListOf()
                        skillLevelMatcher.group(0)?.forEach {
                            numChar -> skillLevels.add(Character.getNumericValue(numChar))
                        }
                    }

                    Log.d(debugTag, "command: $req")
                    return rokRepository.searchEntities(req.replace("[0-9]".toRegex(),"").trim(), skillLevels)

                }
            }

            return null
        }
    }




}

/**




fun processReq(req: String, sendCat: String, forumId: Long): List<String>? {
var req = req
try {
Realm.getDefaultInstance().use { realm ->
val rString = RealmString(req)
val cmd = findCMD(rString)
req = rString.toString() // empty -> null & trim
val calc: PlayWithCat =
label@ PlayWithCat { l: MutableList<String?>, q: String? ->
if (q == null) {
l.add("계산\r\n-----------------\r\nex1) Math.floor(1000/24) 계산냥\r\nex2) 1 + 33 % 2 계산냥")
return@label
}
val mContext: org.mozilla.javascript.Context =
org.mozilla.javascript.Context.enter()
mContext.setOptimizationLevel(-1)
try {
val scope: Scriptable = mContext.initSafeStandardObjects()
val resultObject: Any =
mContext.evaluateString(scope, q, "<cmd>", 1, null)
if (resultObject != null) {
l.add(resultObject.toString())
}
} catch (e: Exception) {
Log.e(TAG, Log.getStackTraceString(e))
l.add("몰랑")
}
}


val searchSkill =
Command { l: MutableList<String?>, q: String?, w: Boolean? ->
if (q == null) {
l.add("\"사령관 이름\"   \"스킬 번호\"   냥 << 이렇게 입력하라옹")
} else {
val number = q.replace(REGEX_EXCEPT_DIGITS.toRegex(), "")
val name = q.replace(REGEX_DIGITS.toRegex(), "")
val commanders: RealmResults<Commander> =
realm.where(Commander::class.java)
.contains(
Commander.FIELD_NAME_WITHOUT_BLANK,
name.replace(REGEX_SPACE.toRegex(), "")
)
.or().equalTo(Commander.FIELD_NAME, name).findAll()
var numberVal = -1
if (!TextUtils.isEmpty(number)) {
numberVal = number.toInt()
if (numberVal > 0 && numberVal < 6) {
numberVal -= 1
} else {
numberVal = -1
}
}
for (commander in commanders) {
val skills: RealmList<Skill> = commander.getSkills()
if (skills != null) {
val skillBuilder: StringBuilder =
StringBuilder(commander.getString(Source.FIELD_NAME))
var firstSearch = true
for (i in 0 until skills.size()) {
val skill: Skill = skills.get(i)
if (skill != null && (numberVal == -1 || numberVal == i)) {
skillBuilder.append(if (firstSearch) "\r\n>" else "\r\n\r\n>")
.append(i + 1).append("스킬: ")
.append(skill.getString(Source.FIELD_NAME))
.append(" (")
.append(skill.getString(Skill.FIELD_PROPERTY))
.append(")\r\n")
.append(
skill.getString(Skill.FIELD_DESCRIPTION)
.replace("<br>", "\r\n")
)
firstSearch = false
}
}
l.add(skillBuilder.toString())
}
}
}
}
val searchCivil =
Command { l: MutableList<String?>, q: String?, w: Boolean? ->
val civilizations: RealmResults<Civilization>
var header = ""
if (q == null) {
civilizations =
realm.where(Civilization::class.java).findAll()
.sort(Source.FIELD_NAME)
header =
"""
총 ${civilizations.size().toString()}개 문명
-----------------
""".trimIndent()
} else {
civilizations = realm.where(Civilization::class.java)
.contains(
Civilization.FIELD_ATTRS.toString() + "." + Attribute.FIELD_NAME_WITHOUT_BLANK,
q
)
.findAll().sort(Source.FIELD_NAME)
if (civilizations.size() > 0) {
header =
"""$q 특성 보유 문명: ${civilizations.size()}개
-----------------"""
}
}
if (civilizations.size() > 0) {
val lambdaResult = StringBuilder()
lambdaResult.append(header)
for (civilization in civilizations) {
lambdaResult.append("\r\n")
.append(civilization.getString(Source.FIELD_NAME))
}
l.add(lambdaResult.toString())
}
}
val searchWiki: Command =
label@ Command { l: MutableList<String?>, q: String?, w: Boolean? ->
if (q == null) {
l.add(ROK_WIKI)
} else {
val commanders: RealmResults<Commander> =
realm.where(Commander::class.java)
.contains(
Commander.FIELD_NAME_WITHOUT_BLANK,
q.replace(REGEX_SPACE.toRegex(), "")
)
.or().equalTo(Commander.FIELD_NAME, q).findAll()
if (commanders.size() === 0) {
return@label
}
for (commander in commanders) {
val commanderId: Int = commander.getId()
if (commanderId > 0) {
val url = ROK_WIKI + ROK_WIKI_HERO_DIR + commanderId
l.add(
commander.getString(Source.FIELD_NAME)
.toString() + ": " + url
)
}
}
}
}
val searchCommanders =
Command { l: MutableList<String?>, q: String?, w: Boolean? ->
var header = ""
var commanders: RealmResults<Commander?>
Log.d(TAG, "searchCommanders Activated")
if (q == null) {
commanders =
realm.where(Commander::class.java).findAll()
.sort(Commander.FIELD_RARITY_ID)
} else {
commanders = realm.where(Commander::class.java)
.equalTo(
Commander.FIELD_RARITY.toString() + "." + Rarity.FIELD_NAME,
q
)
.findAll()
if (commanders.size() > 0) {
header = "희귀도 \"$q\""
} else {
commanders = realm.where(Commander::class.java)
.contains(
Commander.FIELD_SPECS.toString() + "." + Source.FIELD_NAME,
q
)
.findAll()
if (commanders.size() > 0) {
header = "\"$q\" 특성 보유"
}
}
}
if (commanders.size() > 0) {
header += """ 사령관: ${commanders.size().toString()}명
-----------------"""
val lambdaResult = StringBuilder()
lambdaResult.append(header)
for (commander in commanders) {
lambdaResult.append("\r\n")
.append(commander.getString(Source.FIELD_NAME))
}
l.add(lambdaResult.toString())
}
}
val civilByName: PlayWithCat =
label@ PlayWithCat { l: MutableList<String?>, q: String? ->
if (q == null) {
return@label
}
Log.d(TAG, "civilByName Activated")
val civilizations: RealmResults<Civilization> =
realm.where(Civilization::class.java)
.equalTo(Civilization.FIELD_NAME, q.trim { it <= ' ' }).findAll()
for (civilization in civilizations) {
l.add(civilization.getInfo())
}
}
// 사령관 이름으로 정보 검색 : 알렉산더 냥
val commanderByName: PlayWithCat =
label@ PlayWithCat { l: MutableList<String?>, q: String? ->
if (q == null) {
return@label
}
Log.d(TAG, "commanderByName Activated")
val commanders: RealmResults<Commander> = realm.where(Commander::class.java)
.contains(
Commander.FIELD_NAME_WITHOUT_BLANK,
q.replace(REGEX_SPACE.toRegex(), "")
)
.or().equalTo(Commander.FIELD_NAME, q).findAll()
if (commanders.size() === 0) {
return@label
}
for (commander in commanders) {
l.add(commander.getInfo())
}
}
val skillByName: PlayWithCat =
label@ PlayWithCat { l: MutableList<String?>, q: String? ->
if (q == null) {
return@label
}
val skills: RealmResults<Skill> =
if (l.size == 0) realm.where(Skill::class.java)
.equalTo(Source.FIELD_NAME, q)
.or().contains(
Skill.FIELD_NAME_WITHOUT_BLANK,
q.replace(REGEX_SPACE.toRegex(), "")
).findAll() else realm.where(
Skill::class.java
).equalTo(Source.FIELD_NAME, q).findAll()
if (skills.size() > 0) {
for (skill in skills) {
val skill_info = StringBuilder()
val commander: Commander =
realm.where(Commander::class.java).equalTo(
Commander.FIELD_SKILLS.toString() + "." + Source.FIELD_ID,
skill.getId()
).findFirst()
if (commander != null) {
skill_info.append(commander.getString(Source.FIELD_NAME))
.append(" > ")
.append(commander.getNumberOfSkill(skill.getId()))
.append("스킬\r\n")
}
skill_info.append(skill.getString(Source.FIELD_NAME))
.append(" (").append(skill.getString(Skill.FIELD_PROPERTY))
.append(")\r\n")
val desc: String = skill.getString(Skill.FIELD_DESCRIPTION)
skill_info.append(desc.replace("<br>", "\r\n"))
l.add(skill_info.toString())
}
}
}

val findItem: PlayWithCat =
label@ PlayWithCat { l: MutableList<String?>, q: String? ->
if (q == null) {
val itemListBuilder = StringBuilder()
val itemList: RealmResults<Item> =
realm.where(Item::class.java).findAll()
.sort(Item.FIELD_RARITY_ID, Sort.DESCENDING)
.sort(Item.FIELD_SET_ID)
itemListBuilder.append("전체 장비: ").append(itemList.size())
.append("개\r\n-----------------")
.append(printItemList(itemList, null, null, null))
l.add(itemListBuilder.toString())
return@label
}
val qWithoutBlank = q.replace("\\s+".toRegex(), "")
var items: RealmResults<Item?> = realm.where(Item::class.java)
.contains(Item.FIELD_NAME_WITHOUT_BLANK, qWithoutBlank).findAll()
val qList: List<String> =
ArrayList(
Arrays.asList(
 *q.split("\\s+").toTypedArray()
)
)
if (items.size() === 0) {
val itemRealmQuery: RealmQuery<Item> =
realm.where(Item::class.java).alwaysFalse()
for (itemName in qList) {
itemRealmQuery.or().equalTo(Item.FIELD_NAME_WITHOUT_BLANK, itemName)
}
items = itemRealmQuery.findAll()
}
if (items.size() === 0) {
val category: ItemCategory = realm.where(ItemCategory::class.java)
.equalTo(Source.FIELD_NAME, qList[qList.size - 1]).findFirst()
if (category != null) {
qList.removeAt(qList.size - 1)
}
var rarity: Rarity? = null
if (qList.size > 0) {
rarity = realm.where(Rarity::class.java)
.equalTo(Source.FIELD_NAME, qList[qList.size - 1]).findFirst()
if (rarity != null) {
qList.removeAt(qList.size - 1)
}
}
if (qList.size > 0) {
val bases: MutableList<Int> =
ArrayList()
val attrList: MutableList<Array<Attribute>> =
ArrayList<Array<Attribute>>()
for (attrName in qList) {
if (attrName.length > 1) {
val attrs: Array<Attribute> =
realm.where(Attribute::class.java)
.contains(
Attribute.FIELD_NAME_WITHOUT_BLANK,
attrName
)
.findAll().toArray(arrayOfNulls<Attribute>(0))
if (attrs.size > 0) {
bases.add(attrs.size)
attrList.add(attrs)
}
}
}
if (bases.size > 0) {
val mbn = MultiBaseNotation(bases)
val iCombs: List<IntArray> =
mbn.getPositiveBaseDigitsCombination(99999)
for (indexes in iCombs) {
val attrIds = arrayOfNulls<Int>(bases.size)
val attrNames =
arrayOfNulls<String>(bases.size)
var i = 0
while (i < indexes.size) {
val attr: Attribute = attrList[i][indexes[i]]
attrIds[i] = attr.getId()
attrNames[i] = attr.getString(Source.FIELD_NAME)
i++
}
val itemRealmQuery: RealmQuery<Item> =
realm.where(Item::class.java).alwaysTrue()
if (category != null) itemRealmQuery.and()
.equalTo(Item.FIELD_CATEGORY_ID, category.getId())
if (rarity != null) itemRealmQuery.and()
.equalTo(Item.FIELD_RARITY_ID, rarity.getId())
for (attrId in attrIds) {
itemRealmQuery.and().equalTo(
Item.FIELD_ATTRS.toString() + "." + Source.FIELD_ID,
attrId
)
}
val itemsByAttr: RealmResults<Item> =
itemRealmQuery.findAll()
val itemByAttrBuilder =
StringBuilder()
itemByAttrBuilder.append("장비 속성 검색: ")
.append(itemsByAttr.size())
.append("개\r\n")
for (attrName in attrNames) {
itemByAttrBuilder.append("*").append(attrName)
.append("\r\n")
}
if (category != null) itemByAttrBuilder.append("*분류: ")
.append(category.getString(Source.FIELD_NAME))
.append("\r\n")
if (rarity != null) itemByAttrBuilder.append("*희귀도: ")
.append(rarity.getString(Source.FIELD_NAME))
.append("\r\n")
itemByAttrBuilder.append("-----------------")
itemByAttrBuilder.append(
printItemList(
itemsByAttr,
rarity,
category,
attrIds
)
)
if (itemsByAttr.size() > 0) {
l.add(itemByAttrBuilder.toString())
}
}
}
} else {
val itemListBuilder = StringBuilder()
val itemRealmQuery: RealmQuery<Item> = realm.where(Item::class.java)
if (rarity != null) {
itemListBuilder.append(rarity.getString(Source.FIELD_NAME))
.append(" ")
itemRealmQuery.equalTo(Item.FIELD_RARITY_ID, rarity.getId())
}
if (category != null) {
itemListBuilder.append(category.getString(Source.FIELD_NAME))
.append(" ")
itemRealmQuery.equalTo(Item.FIELD_CATEGORY_ID, category.getId())
}
val itemList: RealmResults<Item> = itemRealmQuery.findAll()
.sort(Item.FIELD_RARITY_ID, Sort.DESCENDING)
.sort(Item.FIELD_SET_ID)
if (itemList.size() > 0) {
itemListBuilder.append(if (category != null) ": " else "장비: ")
.append(itemList.size())
.append("개\r\n-----------------")
.append(printItemList(itemList, rarity, category, null))
l.add(itemListBuilder.toString())
}
}
return@label
}
val setMap: MutableMap<ItemSet?, Int> =
HashMap<ItemSet?, Int>()
val equippedMap: MutableMap<ItemCategory?, Item> =
HashMap<ItemCategory?, Item>()
val attrSumMap: MutableMap<Attribute?, Double> =
HashMap<Attribute?, Double>()
val rAttrSumMap: MutableMap<Attribute?, Double> =
HashMap<Attribute?, Double>()
var overlappedEquipment = false
for (item in items) {
val itemInfoBuilder = StringBuilder()
val rarity: Rarity = item.getRarity()
val cate: ItemCategory = item.getCategory()
if (equippedMap.containsKey(cate)) {
overlappedEquipment = true
}
equippedMap[cate] = item
itemInfoBuilder.append("[")
.append(if (cate != null) cate.getString(Source.FIELD_NAME) else "장비")
.append("] ").append(item.getString(Source.FIELD_NAME))
val nameEng: String = item.getString(Source.FIELD_NAME_ENG)
if (nameEng != null) {
itemInfoBuilder.append("\r\n").append(nameEng)
}
val itemSet: ItemSet = item.getItemSet()
if (itemSet != null) {
itemInfoBuilder.append("\r\n")
.append(itemSet.getString(Source.FIELD_NAME))
var setCount: Int
setCount = if (setMap.containsKey(itemSet)) {
val currCount = setMap[itemSet]
(currCount ?: 0) + 1
} else {
1
}
setMap[itemSet] = setCount
}
itemInfoBuilder.append("\r\n희귀도: ")
.append(if (rarity != null) rarity.getString(Source.FIELD_NAME) else "??")
.append(" (Lv.").append(item.getInt(Item.FIELD_LEVEL)).append(")")
val materials: RealmList<ItemMaterial> = item.getMaterials()
val materialCounts: RealmList<RealmInteger> = item.getMaterialCounts()
var secondsSum = 0
run {
var i = 0
var number = 1
while (i < materials.size()) {
val material: ItemMaterial = materials.get(i)
if (material != null) {
itemInfoBuilder.append("\r\n재료")
.append(if (materials.size() === 1) "" else number++)
.append(": ")
val countObj: RealmInteger = materialCounts.get(i)
itemInfoBuilder.append(material.getString(Source.FIELD_NAME))
.append(
if (countObj != null) " " + countObj.getValue()
.toString() + "개" else ""
)
if (countObj != null) {
secondsSum += material.getInt(ItemMaterial.FIELD_SECONDS) * countObj.getValue()
}
}
i++
}
}
if (secondsSum > 0) {
itemInfoBuilder.append("\r\n생산(재료 보급): ")
.append(RokCalcUtils.secondsToString(secondsSum * 2 / 3))
.append("(")
.append(RokCalcUtils.secondsToString(secondsSum * 2 / 5))
.append(")")
}
val gold: Int = item.getInt(Item.FIELD_GOLD)
itemInfoBuilder.append("\r\n금화: ")
.append(RokCalcUtils.quantityToString(gold))
val attrs: RealmList<Attribute> = item.getAttrs()
val attrVals: RealmList<RealmDouble> = item.getAttrVals()
var multiVals: Array<Any?>? = null
if (attrs.size() === 1 && attrVals.size() > 1) {
multiVals = arrayOfNulls(attrVals.size())
var i = 0
while (i < multiVals.size) {
val valObj: RealmDouble = attrVals.get(i)
if (valObj != null) {
multiVals[i] = valObj.getValue()
}
i++
}
}
var i = 0
var number = 1
while (i < attrs.size()) {
val attr: Attribute = attrs.get(i)
if (attr != null) {
itemInfoBuilder.append("\r\n속성")
.append(if (attrs.size() === 1) "" else number++)
.append(": ")
if (multiVals == null) {
val valObj: RealmDouble = attrVals.get(i)
if (valObj != null) {
val `val`: Double = valObj.getValue()
itemInfoBuilder.append(attr.getFormWithValue(`val`))
val curValSum = attrSumMap[attr]
attrSumMap[attr] = (curValSum ?: 0.0) + `val`
val rVal =
Math.ceil(`val` * 0.6) / 2.0
itemInfoBuilder.append(" (+").append(rVal).append(")")
val rValSum = rAttrSumMap[attr]
rAttrSumMap[attr] = (rValSum ?: 0.0) + rVal
} else {
itemInfoBuilder.append(attr.getString(Attribute.FIELD_NAME))
}
} else {
itemInfoBuilder.append(attr.getFormWithValue(multiVals))
}
}
i++
}
val desc: String = item.getString(Item.FIELD_DESCRIPTION)
if (desc != null) {
itemInfoBuilder.append("\r\n").append(desc)
}
if (items.size() < 3) {
l.add(itemInfoBuilder.toString())
}
} // for Items
for (itemSet in setMap.keys) {
val count = setMap[itemSet]
if (count != null && count > 0) {
val attrs: RealmList<Attribute> = itemSet.getAttrs()
val vals: RealmList<RealmDouble> = itemSet.getVals()
val counts: RealmList<RealmInteger> = itemSet.getCounts()
val itemSetBuilder = StringBuilder()
itemSetBuilder.append(itemSet.getString(Source.FIELD_NAME))
.append("\r\n")
.append(itemSet.getString(Source.FIELD_NAME_ENG))
var i = 0
while (i < counts.size()) {
val setCount: RealmInteger = counts.get(i)
val attr: Attribute = attrs.get(i)
val `val`: RealmDouble = vals.get(i)
if (setCount != null && attr != null && `val` != null) {
itemSetBuilder.append("\r\n").append(setCount.getValue())
.append("개: ")
.append(attr.getFormWithValue(`val`.getValue()))
if (count >= setCount.getValue()) {
itemSetBuilder.append(" [o]")
val curVal = attrSumMap[attr]
attrSumMap[attr] = (curVal ?: 0.0) + `val`.getValue()
} else {
itemSetBuilder.append(" [x]")
}
}
i++
}
l.add(itemSetBuilder.toString())
}
}
if (!overlappedEquipment && items.size() > 1) {
val equipBuilder = StringBuilder()
equipBuilder.append("*장비 착용")
for (cate in equippedMap.keys) {
val eqItem: Item? = equippedMap[cate]
if (eqItem != null) {
equipBuilder.append("\r\n - ")
.append(cate.getString(Source.FIELD_NAME))
.append(": ")
.append(eqItem.getString(Source.FIELD_NAME))
}
}
if (attrSumMap.size > 0) {
val sortedKeys: ArrayList<Attribute> =
ArrayList<Any?>(attrSumMap.keys)
Collections.sort(sortedKeys)
equipBuilder.append("\r\n-----------------\r\n*속성 총합")
.append(if (setMap.size > 0) "(세트 효과 포함)" else "")
for (attr in sortedKeys) {
val `val` = attrSumMap[attr]
val rVal = rAttrSumMap[attr]
val valRange =
if (`val` == null) null else `val`.toString() + if (rVal != null) "~" + (`val` + rVal) else ""
equipBuilder.append("\r\n - ")
.append(attr.getFormWithValue(valRange))
}
}
l.add(equipBuilder.toString())
}
}
val searchMemo: PlayWithCat =
label@ PlayWithCat { l: MutableList<String?>, r: String? ->
if (r == null) {
return@label
}
if (r.length > 1 && r.length < 21) {
r = r.replace("\\s+".toRegex(), "")
val memos: RealmResults<Memo> =
realm.where(Memo::class.java)
.contains(Memo.FIELD_NAME_WITHOUT_BLANK, r)
.findAll()
for (memo in memos) {
l.add(printMemo(memo))
}
}
}
val memoCommand: Command =
label@ Command { l: MutableList<String?>, r: String?, s: Boolean? ->
if (r == null) {
val memos: RealmResults<Memo> =
realm.where(Memo::class.java).equalTo(Memo.FIELD_SEND_CAT, sendCat)
.findAll()
if (memos.size() > 1) {
val memoListBuilder = StringBuilder()
memoListBuilder.append(sendCat).append("님의 메모 목록: ")
.append(memos.size()).append("개\r\n-----------------")
var number = 1
val calendar = Calendar.getInstance()
for (memo in memos.sort(Memo.FIELD_WHEN, Sort.DESCENDING)) {
calendar.timeInMillis = memo.getWhen()
memoListBuilder.append("\r\n").append(number++).append(". ")
.append(memo.getName()).append(" ")
.append(
SimpleDateFormat(
"yyyy.MM.dd",
Locale.KOREA
).format(calendar.time)
)
}
l.add(memoListBuilder.toString())
} else if (memos.size() === 1) {
l.add(printMemo(memos.first()))
} else {
l.add(
"""
 *메모 등록 : [제목] + (줄바꿈) + [내용] + 메모 냥
 *메모 내용 : [제목] + 냥 (빈칸 무관)
 *메모 삭제 : [제목] + 메모 냥 (빈칸 일치)
""".trimIndent()
)
}
return@label
}
val readLineRegex =
System.getProperty("line.separator")
if (readLineRegex != null) {
val lines =
r.split(readLineRegex).toTypedArray()
if (lines.size >= 1) {
val memoName = lines[0].trim { it <= ' ' }
var content: String? = null
if (lines.size > 1) {
val contentList: List<String?> =
ArrayList(
Arrays.asList(*lines)
.subList(1, lines.size)
)
content = TextUtils.join("\r\n", contentList).trim { it <= ' ' }
}
if (memoName.length < 21 && memoName.length > 1) {
val memo: Memo =
realm.where(Memo::class.java)
.equalTo(Memo.FIELD_NAME, memoName)
.findFirst()
if (memo != null) {
val editorName: String = memo.getSendCat()
realm.beginTransaction()
if (content == null) {
memo.deleteFromRealm()
realm.commitTransaction()
l.add(editorName + "님의 메모 [" + memoName + "]: 삭제 되었습니다.")
} else {
memo.setSendCat(sendCat)
memo.setWhen(System.currentTimeMillis())
memo.setForumId(forumId)
memo.setContent(content)
realm.commitTransaction()
l.add(editorName + "님의 메모 [" + memoName + "]: 수정 되었습니다.")
}
} else if (content != null) {
realm.beginTransaction()
realm.copyToRealmOrUpdate(
Memo(
memoName,
content,
sendCat,
forumId
)
)
realm.commitTransaction()
l.add(sendCat + "님의 메모 [" + memoName + "]: 추가 완료")
} else {
l.add("메모 추가 실패: 내용을 입력하세요.")
}
} else {
l.add("메모 제목 길이 제한: 2 ~ 20자 ")
}
}
} else {
l.add("시스템 오류: 줄바꿈 지원 안됨")
}
}


val result: MutableList<String> =
ArrayList()
when (cmd) {
CMD_ENUM.CMD_WIKI -> {
searchWiki.search(result, req, false)
searchMemo.play(result, req + CMD_CERTAIN[cmd.ordinal])
}
CMD_ENUM.CMD_ITEM -> {
findItem.play(result, req)
searchMemo.play(result, req + CMD_CERTAIN[cmd.ordinal])
}
CMD_ENUM.CMD_SPEC -> result.add("사령관 특성: 정보 수집 중")
CMD_ENUM.CMD_SKILL -> {
searchSkill.search(result, req, false)
searchMemo.play(result, req + CMD_CERTAIN[cmd.ordinal])
}
CMD_ENUM.CMD_CIVIL -> {
searchCivil.search(result, req, false)
searchMemo.play(result, req + CMD_CERTAIN[cmd.ordinal])
}
CMD_ENUM.CMD_COMMANDER -> {
searchCommanders.search(result, req, false)
searchMemo.play(result, req + CMD_CERTAIN[cmd.ordinal])
}
CMD_ENUM.CMD_RESEARCH -> {
research.search(result, req, true)
searchMemo.play(result, req + CMD_CERTAIN[cmd.ordinal])
}

CMD_ENUM.CMD_CALC -> {
calc.play(result, req)
searchMemo.play(result, req + CMD_CERTAIN[cmd.ordinal])
}
CMD_ENUM.CMD_MEMO -> memoCommand.search(result, req, false)

else -> {
commanderByName.play(result, req)
civilByName.play(result, req)
skillByName.play(result, req)
findItem.play(result, req)
searchMemo.play(result, req)
}
}
return result
}
} catch (e: RuntimeException) {
Log.e(TAG, Log.getStackTraceString(e))
}
return null
}

private fun findOrCreateUser(sendCat: String, forumId: Long, realm: Realm): RokUser? {
var user: RokUser? =
realm.where(RokUser::class.java).equalTo(RokUser.FIELD_FORUM_ID, forumId)
.and().equalTo(RokUser.FIELD_SENDCAT, sendCat)
.and().equalTo(RokUser.FIELD_NAME, sendCat).findFirst()
if (user == null) {
realm.beginTransaction()
user = RokUser(sendCat, sendCat, forumId)
realm.copyToRealm(user)
realm.commitTransaction()
}
return user
}

private fun printItemList(
itemList: RealmResults<Item>,
rarity: Rarity?,
category: ItemCategory?,
attrIds: Array<Int>?
): String? {
val listBuilder = StringBuilder()
for (itemByAttr in itemList.sort(Item.FIELD_RARITY_ID)) {
listBuilder.append("\r\n")
val cateAndRarity: MutableList<String?> = ArrayList()
if (rarity == null) cateAndRarity.add(itemByAttr.getString(Item.FIELD_RARITY))
if (category == null) cateAndRarity.add(itemByAttr.getString(Item.FIELD_CATEGORY))
if (cateAndRarity.size > 0) {
listBuilder.append(TextUtils.join("/", cateAndRarity))
}
listBuilder.append(" - ").append(itemByAttr.getString(Source.FIELD_NAME))
if (attrIds != null && attrIds.size > 0) {
listBuilder.append(" : ")
val valList: MutableList<String?> = ArrayList()
for (attrId in attrIds) {
valList.addAll(Arrays.asList(itemByAttr.getValsStrOfAttr(attrId)))
}
listBuilder.append(TextUtils.join(", ", valList))
}
}
return listBuilder.toString()
}

private fun printMemo(memo: Memo?): String? {
if (memo == null) {
return null
}
val calendar = Calendar.getInstance()
calendar.timeInMillis = memo.getWhen()
return """[메모] ${memo.getName().toString()}
${memo.getContent().toString()}
- """ +
SimpleDateFormat("yyyy년 MM월 dd일 aa hh:mm:ss", Locale.KOREA)
.format(calendar.time).toString() +
"\r\n - " + memo.getSendCat()
}

private fun calcTimeLimit(request: String, timePattern: String, timeUnit: Long): Long {
val pattern = Pattern.compile(timePattern)
val matcher = pattern.matcher(request)
if (matcher.find()) {
val str = matcher.group(0)
if (str != null) {
return NumberUtils.toInt(
str.replace(REGEX_EXCEPT_DIGITS.toRegex(), ""),
0
) * timeUnit
}
}
return 0L
}


}
 */