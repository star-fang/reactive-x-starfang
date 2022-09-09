package com.rx.starfang.database.room.talk

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TalkRepository(private val talkDao: TalkDao) {
    val allConversations: Flow<List<Conversation>> = talkDao.getAllConversations()

    @WorkerThread
    suspend fun insertConversation(name: String, content: String, isUser: Boolean) {
        talkDao.insertConversation( Conversation(0, name, content, isUser, System.currentTimeMillis()))
    }

    @WorkerThread
    suspend fun searchMemo(name: String): List<String> {
        val memoInfoList = mutableListOf<String>()
        talkDao.searchMemoByName(name).forEach {
            memoInfoList.add(StringBuilder().append(it.name).append("\r\n")
                .append(it.content)
                .append("\r\n\r\n작성: ${it.creator}(${it.createTime})")
                .append(it.reviseTime?.run { "\r\n수정: ${it.reviser ?: "?"}($this)" }).toString())
        }
        return memoInfoList
    }
}