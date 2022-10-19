package com.rx.starfang.database.room.memo

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class MemoRepository(private val memoDao: MemoDao) {

    val allMemoName: Flow<List<String>> = memoDao.getAllMemoNames()

    @WorkerThread
    suspend fun insertMemo(name: String, content: String, writer: String, time:Long): String {
        memoDao.insertOrUpdateMemo(name,content,writer,time)?.run {
            return "${creator}님의 메모 [$name]: ${if(creator!=writer) "${writer}님에 의해" else ""} 수정되었습니다."
        }
        return "${writer}님의 메모 [$name]: 저장되었습니다."
    }

    @WorkerThread
    suspend fun deleteMemo(name: String): String {
        memoDao.searchAndDeleteMemo(name)?.run {
            return "${creator}님의 메모 [$name]: 삭제되었습니다."
        }
        return "메모 [$name]: 목록에서 찾을 수 없습니다. 띄어쓰기를 정확하게 일치시켜 주세요."
    }

    @WorkerThread
    suspend fun searchMemos(name: String): List<String> {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd aa hh:mm", Locale.KOREA)
        val memoInfoList = mutableListOf<String>()
        memoDao.searchMemosContainsName(name).forEach {
            memoInfoList.add(StringBuilder().append(it.name).append("\r\n")
                .append(it.content)
                .append("\r\n\r\n작성: ${it.creator}(${dateFormat.format(it.createTime)})")
                .append(it.reviseTime?.run { "\r\n수정: ${it.reviser ?: "?"}(${dateFormat.format(this)})" }).toString())
        }
        return memoInfoList
    }
}