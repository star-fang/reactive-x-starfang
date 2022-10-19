package com.rx.starfang.database.room.talk

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TalkRepository(private val talkDao: TalkDao) {
    val allConversations: Flow<List<Conversation>> = talkDao.getAllConversations()

    @WorkerThread
    suspend fun insertConversation(name: String, content: String, isUser: Boolean): Long {
        val time = System.currentTimeMillis()
        talkDao.insertConversation( Conversation(0, name, content, isUser, time))
        return time
    }
}