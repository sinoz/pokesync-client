package io.pokesync.core.game.ui.element

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

/**
 * A button to close a window.
 * @author Sino
 */
class CloseWindowButton(skin: Skin, pressed: () -> Unit) : Button(skin, "close-button") {
    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                pressed()
            }
        })
    }
}