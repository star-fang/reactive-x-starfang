package com.rx.starfang.ui.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rx.starfang.database.room.talk.Conversation
import com.rx.starfang.databinding.RowTalkBinding
import java.text.SimpleDateFormat
import java.util.*

class TalkAdapter: ListAdapter<Conversation, TalkAdapter.TalkViewHolder>(TALKS_COMPARATOR) {
    companion object {
        private val  TALKS_COMPARATOR = object : DiffUtil.ItemCallback<Conversation>() {
            override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
                return oldItem.id == newItem.id
            }
        }

    }
    open class TalkViewHolder(private val binding: RowTalkBinding): RecyclerView.ViewHolder(binding.root) {
        lateinit var senderView: AppCompatTextView
        lateinit var contentView: AppCompatTextView
        lateinit var timeView: AppCompatTextView

        open fun bind( conversation: Conversation, prevSameDay: Boolean, nextSameDay: Boolean) {
            senderView = binding.textSender
            contentView = binding.textTalkContent
            timeView = binding.textTalkTime

            itemView.layoutDirection = if(conversation.isUser) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
            senderView.visibility = if(prevSameDay) View.GONE else {
                senderView.text = conversation.name
                View.VISIBLE
            }
            timeView.visibility = if(nextSameDay) View.GONE else {
                timeView.text = SimpleDateFormat("aa hh:mm", Locale.KOREA).format(
                    Date(conversation.timeAdded)
                )
                View.VISIBLE
            }
            contentView.text = conversation.content

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TalkViewHolder {
        return TalkViewHolder(RowTalkBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: TalkViewHolder, position: Int) {
        val currTalk = getItem(position)
        holder.bind(currTalk,
            if(position > 0) areMinutesSame(currTalk, getItem(position-1)) else false,
            if(position < itemCount - 1) areMinutesSame(currTalk,getItem(position+1)) else false
        )
    }

    private fun areMinutesSame(oldTalk: Conversation, newTalk: Conversation): Boolean {
        if(oldTalk.isUser == newTalk.isUser) {
            if(oldTalk.name == newTalk.name) {
                return oldTalk.timeAdded / 60000 == newTalk.timeAdded / 60000
            }
        }
        return false
    }
}