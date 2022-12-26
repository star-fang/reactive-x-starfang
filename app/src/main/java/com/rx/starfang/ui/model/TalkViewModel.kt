package com.rx.starfang.ui.model

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.rx.starfang.database.room.memo.MemoRepository
import com.rx.starfang.database.room.rok.RokRepository
import com.rx.starfang.database.room.talk.Conversation
import com.rx.starfang.database.room.talk.TalkRepository
import com.rx.starfang.nlp.NlpPreprocessing
import com.rx.starfang.nlp.RokLambda
import com.rx.starfang.nlp.TalkLambda
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class TalkViewModel(
    private val talkRepo: TalkRepository,
    private val rokRepo: RokRepository,
    private val memoRepo: MemoRepository
) : ViewModel() {
    companion object {
        private val TAG = TalkViewModel::class.java.simpleName
    }

    val allTalks: LiveData<List<Conversation>> = talkRepo.allConversations.asLiveData()

    fun insertTalk(name: String, content: String) = viewModelScope.launch {


        //https://stackoverflow.com/questions/10246030/stopping-the-rhino-engine-in-middle-of-execution
        val time: Long = talkRepo.insertConversation(name, content, true)
        NlpPreprocessing.preProc(content)?.run {
            RokLambda.process(trim(), name, rokRepo)?.forEach { answer ->
                talkRepo.insertConversation("냥", answer, false)
            }

            val daemon = thread(isDaemon = true) {

                Log.d(TAG, "daemon start")
                /*
                var a = 0;
                while (true) {
                    if (a > 10)
                        break
                    Log.d(TAG, "thread: " + a++.toString())
                    Thread.sleep(500)
                }
                 */

                try {
                    runBlocking {
                        launch {
                            Log.d(TAG, "coroutine launched")
                            TalkLambda.process(trim(), name, memoRepo, time)?.forEach { answer ->
                                talkRepo.insertConversation("멍", answer, false)
                            }
                            Log.d(TAG, "coroutine end normally")
                        }
                    }
                }catch (e: InterruptedException) {
                    Log.e(TAG, Log.getStackTraceString(e))
                    launch {
                        talkRepo.insertConversation("멍", e.toString(), false)
                    }

                }

                Log.d(TAG, "daemon end")
            }
            Log.d(TAG, "start countdown(2s)")
            delay(2000L)
            Log.d(TAG, "countdown end(2s)")



            if (daemon.isAlive) {
                Log.d(TAG, "daemon is alive: let's kill it!")
                daemon.interrupt()
            }


            //delay(2000L)
            //calcJob.cancelAndJoin()
            //talkRepo.insertConversation("멍", "timeout(2s)", false)

        }
    }
}

class TalkViewModelFactory(
    private val talkRepo: TalkRepository,
    private val rokRepo: RokRepository,
    private val memoRepo: MemoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(TalkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TalkViewModel(talkRepo, rokRepo, memoRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class(${modelClass.name})")
    }
}