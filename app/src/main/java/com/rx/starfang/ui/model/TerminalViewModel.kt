package com.rx.starfang.ui.model

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.rx.starfang.database.room.memo.MemoRepository
import com.rx.starfang.database.room.rok.RokRepository
import com.rx.starfang.database.room.terminal.TerminalRepository
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class TerminalViewModel(
    private val memoRepo: MemoRepository,
    private val terminalRepo: TerminalRepository,
    private val rokRepo: RokRepository
) : ViewModel() {
    //val allLines: LiveData<List<Line>> = terminalRepo.allLines.asLiveData()
    val allMemoNames = memoRepo.allMemoName.asLiveData()
    fun getCurrLines(time: Long) = terminalRepo.getCurrLines(time).asLiveData()

    fun updateCommand(id: Long, command: String) = viewModelScope.launch {
        terminalRepo.updateCommand(id, command)
    }

    fun updateMessage(id: Long, message: String) = viewModelScope.launch {
        terminalRepo.updateMessage(id, message)
    }

    fun addCommandLine() = viewModelScope.launch {
        terminalRepo.insertLine("","")
    }

    fun insert(command: String?, message: String?, timeAdded: Long? = null) =
        viewModelScope.launch {
            if (timeAdded is Long)
                terminalRepo.insertLine(command, message, timeAdded)
            else
                terminalRepo.insertLine(command, message)
        }

    fun insertChangingMessageLine(lifecycleOwner: LifecycleOwner, lineIdLiveData: MutableLiveData<Long>, messageLiveData:MutableLiveData<String>, observer: Observer<String>) = viewModelScope.launch {
        lineIdLiveData.postValue(terminalRepo.insertMessage(messageLiveData.value ?: ""))
        messageLiveData.observe(lifecycleOwner, observer)
    }


    fun insertRokEntity(entity: Any, clazz: KClass<*>) = viewModelScope.launch {
        rokRepo.insertEntity(entity, clazz)
    }

    fun insertMemo(name: String, content: String, creator: String, time: Long) =
        viewModelScope.launch {
            memoRepo.insertMemo(name, content, creator, time)
        }




    //■‣□□□□□□□□□□□□
    //■■■■■■■‣□□□□□□


}

class TerminalViewModelFactory(
    private val memoRepo: MemoRepository,
    private val repository: TerminalRepository,
    private val rokRepo: RokRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(TerminalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TerminalViewModel(memoRepo, repository, rokRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}