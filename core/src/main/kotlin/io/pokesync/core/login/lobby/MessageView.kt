package io.pokesync.core.login.lobby

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table

/**
 * A view of all lobby chat messages.
 * @author Sino
 */
class MessageView(skin: Skin) : Table(skin) {
    fun addMessageLabel(sender: String, payload: String) {
        val message = Label("$sender: $payload", skin, "roboto-mono-pt11-black")
        message.setWrap(true)

        add(message)
            .expandX()
            .fillX()
            .left()
            .bottom()
            .row()
    }
}