package com.rx.starfang.ui.terminal

import androidx.lifecycle.*
import com.rx.starfang.database.room.Line
import com.rx.starfang.database.room.TerminalRepository
import kotlinx.coroutines.launch

class TerminalViewModel(private val repository: TerminalRepository): ViewModel() {
    val allLines: LiveData<List<Line>> = repository.allLines.asLiveData()
    fun getCurrLines(time:Long) = repository.getCurrLines(time).asLiveData()

    fun updateCommand(id:Long, command:String?) = viewModelScope.launch {
        repository.updateCommand(id, command)
    }
    fun insert(line: Line) = viewModelScope.launch {
        repository.insert(line)
    }
}

class TerminalViewModelFactory(private val repository: TerminalRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TerminalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TerminalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}