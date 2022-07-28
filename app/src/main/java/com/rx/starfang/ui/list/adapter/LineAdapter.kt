package com.rx.starfang.ui.list.adapter

import android.content.Intent
import android.content.res.Resources
import android.icu.text.MessageFormat
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.rx.starfang.R
import com.rx.starfang.TerminalActivity
import com.rx.starfang.database.room.terminal.Line
import com.rx.starfang.databinding.RowEditLineBinding
import com.rx.starfang.databinding.RowLineBinding

class LineAdapter : ListAdapter<Line, LineAdapter.LineViewHolder>(LINES_COMPARATOR) {
    companion object {
        private val LINES_COMPARATOR = object : DiffUtil.ItemCallback<Line>() {
            override fun areItemsTheSame(oldItem: Line, newItem: Line): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Line, newItem: Line): Boolean {
                return oldItem.timeAdded == newItem.timeAdded
            }
        }


    }

    open class LineViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var titleView: AppCompatTextView
        lateinit var messageView: AppCompatTextView
        private val rss: Resources = binding.root.resources
        open fun bind(line: Line) {
            if(binding is RowLineBinding) {
                titleView = binding.textLineTitle
                messageView = binding.textAnswer
                binding.textCommand.text = line.command
            } else {
                binding as RowEditLineBinding
                titleView = binding.textLineTitle
                messageView = binding.textAnswer
                binding.textEditCommand.setOnKeyListener(View.OnKeyListener{v, keyCode, evt ->
                    v as AppCompatEditText
                    if( keyCode == KeyEvent.KEYCODE_ENTER && evt.action == KeyEvent.ACTION_UP) {
                        val intent = Intent()
                        intent.action = TerminalActivity.ACTION_ADD_LINE
                        intent.putExtra(TerminalActivity.EXTRAS_ADD_LINE_COMMAND, v.text.toString())
                        v.setText("")
                        intent.putExtra(TerminalActivity.EXTRAS_ADD_LINE_ID, line.id)
                        binding.root.context.sendBroadcast(intent)
                        return@OnKeyListener true
                    }
                    false
                })
            }
            titleView.visibility = if(line.command == null) View.GONE else View.VISIBLE
            val user = rss.getString(R.string.signature_default)
            val host = rss.getString(R.string.host_default)
            val placeholder = rss.getString(R.string.placeholder_default)
            titleView.text = placeholder.let { MessageFormat.format(it, user, host, line.id) }
            messageView.text = line.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(itemCount - 1 == position) {
            return 1
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val itemBinding: ViewBinding = when(viewType) {
            1 -> RowEditLineBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            else ->
                RowLineBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false)
        }
        return LineViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val currLine: Line = getItem(position)
        holder.bind(currLine)
    }
}