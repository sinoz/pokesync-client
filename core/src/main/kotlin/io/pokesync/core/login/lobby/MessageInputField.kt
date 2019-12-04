package io.pokesync.core.login.lobby

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField

/**
 * The input field for the user to enter chat messages into.
 * @author Sino
 */
class MessageInputField(skin: Skin, submitMessage: (String) -> Unit) : TextField("", skin, "chat-input-box") {
    init {
        setTextFieldListener { textField, character ->
            when (character) {
                ENTER -> {
                    if (textField.text.isNotEmpty()) {
                        submitMessage(textField.text)
                        clearText()
                    }
                }
            }
        }
    }

    fun clearText() {
        setText("")
    }

    companion object {
        const val ENTER = '\r'
    }
}