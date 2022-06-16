package com.rx.starfang

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rx.starfang.database.room.Line
import com.rx.starfang.databinding.ActivityTerminalBinding
import com.rx.starfang.ui.list.adapter.LineAdapter
import com.rx.starfang.ui.terminal.TerminalViewModel
import com.rx.starfang.ui.terminal.TerminalViewModelFactory

class TerminalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTerminalBinding

    companion object {
        const val ACTION_ADD_LINE = "ACTION_ADD_LINE"
        const val EXTRAS_ADD_LINE_ID = "EXTRAS_ADD_LINE_ID"
        const val EXTRAS_ADD_LINE_COMMAND = "EXTRAS_ADD_LINE_COMMAND"
    }
    private val terminalViewModel: TerminalViewModel by viewModels {
        TerminalViewModelFactory((application as RxStarfangApp).terminalRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTerminalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = LineAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.terminal_recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val currTime = System.currentTimeMillis()

        terminalViewModel.getCurrLines(currTime).observe(this) {
                lines -> lines.let{
            adapter.submitList(it)
                }
        }

        terminalViewModel.insert(Line(0, currTime,null,"welcome (${currTime})"))
        terminalViewModel.insert(Line(0,System.currentTimeMillis(), "", ""))

        registerReceiver(editLineReceiver, IntentFilter(ACTION_ADD_LINE))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(editLineReceiver)
    }


    private val editLineReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("Terminal Receive", intent.toString())
            when(intent?.action) {
                ACTION_ADD_LINE -> {
                    val command: String? = intent.extras?.getString(EXTRAS_ADD_LINE_COMMAND)
                    intent.extras?.getLong(EXTRAS_ADD_LINE_ID)?.let {
                        terminalViewModel.updateCommand(it, command)
                    }

                    terminalViewModel.insert(Line(0,System.currentTimeMillis(),"", ""))
                }
            }
        }
    }


}