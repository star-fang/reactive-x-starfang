package com.rx.starfang.ui.terminal

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.rx.starfang.database.room.rok.RokRepository
import com.rx.starfang.database.room.terminal.Line
import com.rx.starfang.database.room.terminal.TerminalRepository
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class TerminalViewModel(private val terminalRepo: TerminalRepository, val rokRepo: RokRepository): ViewModel() {
    val allLines: LiveData<List<Line>> = terminalRepo.allLines.asLiveData()
    fun getCurrLines(time:Long) = terminalRepo.getCurrLines(time).asLiveData()

    fun updateCommand(id:Long, command:String?) = viewModelScope.launch {
        terminalRepo.updateCommand(id, command)
    }

    fun updateMessage(id:Long, message: String? ) = viewModelScope.launch {
        terminalRepo.updateMessage(id ,message)
    }

    fun insert(line: Line) = viewModelScope.launch {
        terminalRepo.insertLine(line)
    }

    fun insertRokEntity(entity: Any, clazz: KClass<*>) = viewModelScope.launch {
        rokRepo.insertEntity(entity, clazz)
    }

    fun showCommander(id:Long, name: String) = viewModelScope.launch {
        terminalRepo.updateMessage(id, rokRepo.showCommander(name))
    }
}

class TerminalViewModelFactory(private val repository: TerminalRepository, private val rokRepo: RokRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if(modelClass.isAssignableFrom(TerminalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TerminalViewModel(repository, rokRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}