package com.rx.starfang

import android.app.Application
import com.rx.starfang.database.room.StarfangRoomDatabase
import com.rx.starfang.database.room.TerminalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RxStarfangApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val roomDatabase by lazy { StarfangRoomDatabase.getDatabase(this, applicationScope)}
    val terminalRepository by lazy { TerminalRepository(roomDatabase.terminalDao()) }
}