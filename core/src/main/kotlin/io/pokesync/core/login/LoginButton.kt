package io.pokesync.core.login

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

/**
 * The login button.
 * @author Sino
 */
class LoginButton(skin: Skin, pressed: () -> Unit) : Button(skin, "login-button") {
    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                pressed()
            }
        })
    }
}