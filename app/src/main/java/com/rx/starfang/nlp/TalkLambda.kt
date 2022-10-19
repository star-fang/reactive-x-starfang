package com.rx.starfang.nlp

import android.text.TextUtils
import com.rx.starfang.database.room.memo.MemoRepository
import com.rx.starfang.database.room.talk.TalkRepository
import kotlin.collections.HashMap
import kotlin.math.max

class TalkLambda {

    enum class CmdNum {
        MEMO, CALC
    }

    companion object {

        private const val debugTag = "talk_lambda"


        private val cmdKor2HashMap = HashMap<String, CmdNum>(
            mapOf(
                "메모" to CmdNum.MEMO,
                "계산" to CmdNum.CALC
            )
        )

        private val cmdEngHashMap = HashMap<String, CmdNum>(
            mapOf(
                "memo" to CmdNum.MEMO,
                "calc" to CmdNum.CALC
            )
        )

        suspend fun process(
            content: String,
            sender: String,
            memoRepo: MemoRepository,
            time: Long
        ): List<String>?{
            var req = content

            var cmdNum: CmdNum? = null
            val pivotIndex = max(0, req.length - 2)
            val key = req.substring(pivotIndex)
            cmdKor2HashMap[key]?.run {
                cmdNum = this
                req = req.substring(0, pivotIndex).trim()
            }

            when (cmdNum) {
                CmdNum.MEMO -> {
                    val lineRegex = System.getProperty("line.separator")
                    lineRegex?.run {
                        val lines: Array<String> = req.split(toRegex()).toTypedArray()
                        if (lines.isNotEmpty()) {
                            val memoName: String = lines[0].trim()
                            return List(1) {
                                if (lines.size > 1) {
                                    memoRepo.insertMemo(
                                        memoName,
                                        TextUtils.join(
                                            "\r\n",
                                            listOf(*lines).subList(1, lines.size)
                                        )
                                            .trim(), sender, time
                                    )

                                } else {
                                    memoRepo.deleteMemo(memoName)
                                }
                            }
                        }
                    }
                }
                else -> return memoRepo.searchMemos(req)
            }
            return null
        }
    }


}