package com.rx.starfang.ui

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText

class ExtendedEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    private var mListeners: MutableList<TextWatcher>? = null

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
    }
    override fun addTextChangedListener(watcher: TextWatcher?) {
        watcher?.let {
            if(mListeners == null)
                mListeners = mutableListOf()
            mListeners?.add(it)
            Log.d("editTextExtended", "TextWatcher added" )
        }
        super.addTextChangedListener(watcher)
    }

    override fun removeTextChangedListener(watcher: TextWatcher?) {
        mListeners?.run {
            val watcherIndex: Int = indexOf(watcher)
            if (watcherIndex >= 0) {
                removeAt(watcherIndex)
                Log.d("editTextExtended", "TextWatcher at $watcherIndex removed" )
            }
        }
        super.removeTextChangedListener(watcher)
    }


    fun clearTextChangedListeners() {
        mListeners?.run {
            for(i in indices) {
                super.removeTextChangedListener(get(i))
                Log.d("editTextExtended", "TextWatcher at $i removed" )
            }
            clear()
        }
        mListeners = null
    }

}