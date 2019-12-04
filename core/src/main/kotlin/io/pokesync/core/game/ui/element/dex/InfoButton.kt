package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

/**
 * A button to switch back to the info interface.
 * @author Sino
 */
class InfoButton(skin: Skin, pressed: () -> Unit) : Button(skin, "info-button") {
    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                pressed()
            }
        })
    }
}