package io.pokesync.core.login

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField

/**
 * The password input field in which the user may enter the password of their account.
 * @author Sino
 */
class PasswordField(skin: Skin, pressedEnter: () -> Unit) : TextField("", skin, "credentials-input-box") {
    init {
        setTextFieldListener { textField, character ->
            when (character) {
                ENTER -> {
                    if (textField.text.isNotEmpty()) {
                        pressedEnter()
                    }
                }
            }
        }

        setPasswordMode(true)
        setPasswordCharacter('*')
    }

    companion object {
        const val ENTER = '\r'
    }
}