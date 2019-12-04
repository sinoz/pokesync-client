package io.pokesync.core.login

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

/**
 * The exit button.
 * @author Sino
 */
class ExitButton(skin: Skin, f: () -> Unit) : Button(skin, "exit-button") {
    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                f()
            }
        })
    }
}