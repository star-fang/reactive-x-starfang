package com.rx.starfang.ui.model

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.rx.starfang.database.room.rok.RokRepository
import com.rx.starfang.database.room.talk.Conversation
import com.rx.starfang.database.room.talk.TalkRepository
import com.rx.starfang.nlp.NlpPreprocessing
import com.rx.starfang.nlp.RokLambda
import com.rx.starfang.nlp.TalkLambda
import kotlinx.coroutines.launch

class TalkViewModel(private val talkRepo: TalkRepository, private val rokRepo: RokRepository): ViewModel() {
    val allTalks: LiveData<List<Conversation>> = talkRepo.allConversations.asLiveData()

    fun insertTalk(name: String, content: String) = viewModelScope.launch{
        val time: Long = talkRepo.insertConversation(name, content, true)
        NlpPreprocessing.preProc(content)?.run {
            RokLambda.process(trim(), name, rokRepo)?.forEach { answer ->
                talkRepo.insertConversation("냥", answer, false)
            }
            TalkLambda.process(trim(),name,talkRepo, time)?.forEach { answer ->
                talkRepo.insertConversation("멍", answer, false)
            }
        }
    }
}

class TalkViewModelFactory(private val talkRepo: TalkRepository, private val rokRepo: RokRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if(modelClass.isAssignableFrom(TalkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TalkViewModel(talkRepo, rokRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class(${modelClass.name})")
    }
}