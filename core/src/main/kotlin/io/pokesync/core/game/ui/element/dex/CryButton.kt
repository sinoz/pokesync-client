package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

/**
 * A cry button.
 * @author Sino
 */
class CryButton(skin: Skin, pressed: () -> Unit) : Button(skin, "cry-button") {
    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                pressed()
            }
        })
    }
}