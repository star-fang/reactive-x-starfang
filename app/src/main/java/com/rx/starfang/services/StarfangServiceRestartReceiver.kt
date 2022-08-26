package com.rx.starfang.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class StarfangServiceRestartReceiver : BroadcastReceiver() {

    companion object {
        const val RESTART_REPLY_SERVICE = "restart_reply_service!"
        const val RESTART_FIREBASE_SERVICE = "restart_firebase_service!"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                RESTART_REPLY_SERVICE -> context?.run {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        startForegroundService(Intent(this, StarfangReplyService::class.java))
                    else
                        startService(Intent(this, StarfangReplyService::class.java))
                }
            }
        }
    }

}