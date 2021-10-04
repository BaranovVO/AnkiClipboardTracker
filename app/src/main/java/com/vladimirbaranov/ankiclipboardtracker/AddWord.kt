package com.vladimirbaranov.ankiclipboardtracker

import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.vladimirbaranov.ankiclipboardtracker.translate_api.OnTranslationCompleteListener

class AddWord : AppCompatActivity() {

    private lateinit var clipboard: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)
        clipboard =
            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (clipboard.hasPrimaryClip()) {
                val item = clipboard.primaryClip!!.getItemAt(0)
                val pasteData = item.text
                if (pasteData != null) {
                    val translate = translate_api()
                    translate.setOnTranslationCompleteListener(object :
                        OnTranslationCompleteListener {
                        override fun onStartTranslation() {
                        }

                        override fun onCompleted(text: String) {
                            val shareIntent = ShareCompat.IntentBuilder(this@AddWord)
                                .setType("text/plain")
                                .setText(text)
                                .setSubject(pasteData.toString())
                                .intent

                            shareIntent.setClassName(
                                "com.ichi2.anki",
                                "com.ichi2.anki.NoteEditor"
                            )
                            startActivity(shareIntent)
                            finish()
                        }

                        override fun onError(e: Exception) {
                            finish()
                        }
                    })
                    translate.execute(
                        pasteData.toString(),
                        "en",
                        "ru"
                    )

                }
            }
        }
    }
}