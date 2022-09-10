package com.rx.starfang.nlp

class NlpPreprocessing {
    enum class LangNum {
        KOR, ENG
    }

    companion object {

        private const val commandSfx = "냥"

        fun preProc(contentText: String): String? {
            val textTrimmed = contentText.trim()
            if(textTrimmed.length > commandSfx.length && textTrimmed.substring(textTrimmed.length- commandSfx.length) == commandSfx)
                return textTrimmed.substring(0, textTrimmed.length- commandSfx.length).trim()
            return null
        }
    }

}