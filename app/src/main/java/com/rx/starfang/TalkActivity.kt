package com.rx.starfang

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rx.starfang.databinding.ActivityTalkBinding
import com.rx.starfang.ui.list.adapter.TalkAdapter
import com.rx.starfang.ui.model.TalkViewModel
import com.rx.starfang.ui.model.TalkViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TalkActivity : AppCompatActivity(), CoroutineScope {

    lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityTalkBinding
    private lateinit var mNotificationManager: NotificationManager

    companion object {
        const val CHANNEL_ID = "channel_talk_activity"
        const val CHANNEL_NAME = "Starfang Talk Channel"
    }

    private val talkViewModel: TalkViewModel by viewModels {
        TalkViewModelFactory(
            (application as RxStarfangApp).talkRepository,
            (application as RxStarfangApp).rokRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = Job()
        binding = ActivityTalkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val talkAdapter = TalkAdapter()
        val talkRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_talks)
        talkRecyclerView.adapter = talkAdapter
        talkRecyclerView.layoutManager = LinearLayoutManager(this@TalkActivity)

        talkViewModel.allTalks.observe(this@TalkActivity) { talks ->
            talkAdapter.submitList(talks)
        }

        mNotificationManager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE
                )
            )
        }

        binding.run {
            editTextTalk.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if(TextUtils.isEmpty(s)) {
                        clearText.visibility = View.GONE
                        sendTalk.visibility = View.INVISIBLE
                    } else {
                        if(clearText.visibility != View.VISIBLE)
                            clearText.visibility = View.VISIBLE
                        if(sendTalk.visibility != View.VISIBLE)
                            sendTalk.visibility = View.VISIBLE
                        if(!sendTalk.isEnabled)
                            sendTalk.isEnabled = true
                    }
                }

            })

            clearText.setOnClickListener {
                sendTalk.isEnabled = false
                editTextTalk.setText("")
            }

            sendTalk.setOnClickListener {
                sendTalk.isEnabled = false
                editTextTalk.text.let { editable ->
                    if(TextUtils.isEmpty(editable)){
                        sendTalk.visibility = View.INVISIBLE
                        clearText.visibility = View.GONE
                    } else {
                        editTextTalk.setText("")
                        launch {
                            talkViewModel.insertTalk("규규",
                                editable.toString(),
                                true
                            )


                        }
                    }
                }
            }
        }


    }
}