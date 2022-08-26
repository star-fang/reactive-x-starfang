package com.rx.starfang.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.rx.starfang.RxStarfangApp
import com.rx.starfang.database.room.rok.nlp.RokLambda
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*

class StarfangReplyService : NotificationListenerService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    companion object {
        const val FOREGROUND_ID = 33
        const val FOREGROUND_CHANNEL_ID = "starfang_reply_foreground_channel"
        const val FOREGROUND_CHANNEL_NAME = "Starfang Reply Service"
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService()
        else
            startForeground(FOREGROUND_ID, Notification())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startForegroundService() {
        val channel = NotificationChannel(
            FOREGROUND_CHANNEL_ID,
            FOREGROUND_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLACK
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setContentTitle("")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(FOREGROUND_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
        val restartIntent = Intent()
        restartIntent.action = StarfangServiceRestartReceiver.RESTART_REPLY_SERVICE
        restartIntent.setClass(this, StarfangServiceRestartReceiver::class.java)
        sendBroadcast(restartIntent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.run {
            val replyAction = NotificationReplier.getNotificationAction(notification) ?: return

            Observable.fromCallable {

                var contentTextChars: CharSequence? =
                    notification.extras.getCharSequence(Notification.EXTRA_TEXT)
                contentTextChars =
                    if (!TextUtils.isEmpty(contentTextChars)) notification.extras.getCharSequence(
                        Notification.EXTRA_SUMMARY_TEXT
                    ) else
                        contentTextChars
                val contentText: String =
                    (if (!TextUtils.isEmpty(contentTextChars)) contentTextChars.toString() else null)
                        ?: return@fromCallable "empty text"
                val sendCat: String =
                    notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString()
                //val forumName: String = notification.extras.getCharSequence(Notification.EXTRA_SUB_TEXT).toString()

                val preprocessedContent = RokLambda.preProc(contentText) ?: return@fromCallable "no command"

                scope.launch {
                    val replyList: List<String>? = RokLambda.process(preprocessedContent, sendCat, (application as RxStarfangApp).rokRepository)
                    if (replyList != null && replyList.isNotEmpty()) {
                        val parcelableReplyAction = ParcelableReplyAction(replyAction, true )
                        replyList.forEach {
                            parcelableReplyAction.sendReply(this@StarfangReplyService, it)
                        }
                    }
                }

                return@fromCallable "reply"
            }.observeOn(Schedulers.io())
                .subscribe {
                    //ParcelableReplyAction(this, )
                }

        }
    }
}

class NotificationReplier {
    companion object {
        private val REPLY_KEYWORDS = listOf("reply", "android.intent.extra.text")
        private val INPUT_KEYWORD: CharSequence = "input"

        fun getNotificationAction(notification: Notification): NotificationCompat.Action? {

            getAndroidReplyAction(notification)?.run {
                return this
            }
            getWearReplyAction(notification)?.run {
                return this
            }

            return null
        }

        private fun getAndroidReplyAction(notification: Notification): NotificationCompat.Action? {
            for (i in 1..NotificationCompat.getActionCount(notification)) {
                val action = NotificationCompat.getAction(notification, i - 1)
                if (action != null && action.remoteInputs != null) {
                    for (x in 1..action.remoteInputs!!.size) {
                        val remoteInput = action.remoteInputs!![x - 1]
                        if (isKnownReplyKey(remoteInput.resultKey))
                            return action
                    }
                }
            }
            return null
        }

        private fun getWearReplyAction(notification: Notification): NotificationCompat.Action? {
            val wearableExtender = NotificationCompat.WearableExtender(notification)
            for (action in wearableExtender.actions) {
                action.remoteInputs?.run {
                    for (i in 0 until size) {
                        val remoteInput = this[i]
                        if (isKnownReplyKey(remoteInput.resultKey) || remoteInput.resultKey.lowercase()
                                .contains(
                                    INPUT_KEYWORD
                                )
                        )
                            return action
                    }
                }
            }
            return null
        }

        private fun isKnownReplyKey(resultKey: String): Boolean {
            if (TextUtils.isEmpty(resultKey))
                return false

            val resultKeyLowerCase = resultKey.lowercase()
            for (keyword in REPLY_KEYWORDS)
                if (resultKey.contains(keyword))
                    return true
            return false
        }
    }


}

class ParcelableReplyAction() : Parcelable {
    var pendingIntent: PendingIntent? = null
    var isQuickReply: Boolean = false
    lateinit var remoteInputs: ArrayList<RemoteInputParcel>
    //var sendCat: String? = null
    //var forumName: String? = null
    //var content: String? = null

    constructor(parcel: Parcel) : this() {
        val zeroByte: Byte = 0
        pendingIntent = parcel.readParcelable(PendingIntent::class.java.classLoader)
        isQuickReply = parcel.readByte() != zeroByte
        parcel.readTypedList(remoteInputs, RemoteInputParcel.CREATOR)
        //sendCat = parcel.readString()
        //forumName = parcel.readString()
        //content = parcel.readString()
    }

    constructor(
        action: NotificationCompat.Action,
        //sendCat: String,
        //forumName: String,
        //content: String,
        isQuickReply: Boolean
    ) : this() {
        pendingIntent = action.actionIntent
        //this.sendCat = sendCat
        //this.forumName = forumName
        //this.content = content

        action.remoteInputs?.run {
            for (i in 0 until size) {
                remoteInputs.add(RemoteInputParcel(this[i]))
            }
        }
        this.isQuickReply = isQuickReply
    }

    fun sendReply(context: Context, message: String) {
        val intent = Intent()
        val bundle = Bundle()
        val actualInputs = arrayListOf<RemoteInput>()

        for (input in remoteInputs) {
            bundle.putCharSequence(input.resultKey, message)
            val builder = RemoteInput.Builder(input.resultKey)
            builder.setLabel(input.label)
            builder.setChoices(input.choices)
            builder.setAllowFreeFormInput(input.allowFreeFormInput)
            input.extras?.run {
                builder.addExtras(this)
            }
            actualInputs.add(builder.build())
        }
        val inputs: Array<RemoteInput> = actualInputs.toTypedArray()
        RemoteInput.addResultsToIntent(inputs, intent, bundle)
        pendingIntent?.run {
            send(context, 0, intent)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableReplyAction> {
        override fun createFromParcel(parcel: Parcel): ParcelableReplyAction {
            return ParcelableReplyAction(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableReplyAction?> {
            return arrayOfNulls(size)
        }
    }

}

class RemoteInputParcel() : Parcelable {
    var label: String? = null
    lateinit var resultKey: String
    lateinit var choices: Array<String?>
    var allowFreeFormInput: Boolean = false
    var extras: Bundle? = null

    constructor(parcel: Parcel) : this() {
        val zeroByte: Byte = 0
        label = parcel.readString()
        resultKey = parcel.readString().toString()
        choices = parcel.createStringArray() as Array<String?>
        allowFreeFormInput = parcel.readByte() != zeroByte
        extras = parcel.readParcelable(Bundle::class.java.classLoader)
    }

    constructor(remoteInput: RemoteInput) : this() {
        label = remoteInput.label.toString()
        resultKey = remoteInput.resultKey
        remoteInput.choices?.let { charSequenceToStringArray(it) }
        allowFreeFormInput = remoteInput.allowFreeFormInput
        extras = remoteInput.extras

    }


    private fun charSequenceToStringArray(charSequence: Array<CharSequence>) {
        val size = charSequence.size
        choices = arrayOfNulls(size)
        for (i in 0 until size) {
            choices[i] = charSequence[i].toString()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(label)
        parcel.writeString(resultKey)
        parcel.writeStringArray(choices)
        parcel.writeByte(if (allowFreeFormInput) 1 else 0)
        parcel.writeParcelable(extras, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RemoteInputParcel> {
        override fun createFromParcel(parcel: Parcel): RemoteInputParcel {
            return RemoteInputParcel(parcel)
        }

        override fun newArray(size: Int): Array<RemoteInputParcel?> {
            return arrayOfNulls(size)
        }
    }

}
