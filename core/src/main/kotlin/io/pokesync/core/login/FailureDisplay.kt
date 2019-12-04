package io.pokesync.core.login

import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * Displays the login response to the user.
 * @author Sino
 */
class FailureDisplay(skin: Skin) : Table(skin) {
    private val responseText = Label("", skin, "response-text")

    init {
        background = skin["message-box"]

        responseText.setWrap(true)

        add(responseText)
            .expand()
            .fill(0.95F, 1F)
            .center()

        hide()
    }

    /**
     * Updates the response text.
     */
    fun setResponseText(value: String) {
        responseText.setText(value)
    }

    /**
     * Shows the failure display.
     */
    fun show() {
        isVisible = true

        addAction(sequence(fadeIn(0F), delay(5F), fadeOut(2F)))
    }

    /**
     * Hides this failure display.
     */
    fun hide() {
        isVisible = false
    }
}