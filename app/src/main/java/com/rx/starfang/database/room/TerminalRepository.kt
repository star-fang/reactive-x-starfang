package com.rx.starfang.database.room

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TerminalRepository(private val lineDao: LineDao) {
    val allLines: Flow<List<Line>> = lineDao.getLines(0)
    fun getCurrLines(currTime:Long) = lineDao.getLines(currTime)

    @WorkerThread
    suspend fun updateCommand(id:Long, command:String?) {
        lineDao.updateCommand(id, command)
    }
    @WorkerThread
    suspend fun insert(line: Line) {
        lineDao.addLine(line)
    }
}