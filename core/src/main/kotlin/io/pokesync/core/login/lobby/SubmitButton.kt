package io.pokesync.core.login.lobby

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

/**
 * The submit button to submit an entered chat message.
 * @author Sino
 */
class SubmitButton(skin: Skin, submit: () -> Unit) : TextButton("Send", skin, "chat-send") {
    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                submit()
            }
        })
    }
}