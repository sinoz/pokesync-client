package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

/**
 * A button to switch to the map interface.
 * @author Sino
 */
class MapButton(skin: Skin, pressed: () -> Unit) : Button(skin, "map-button") {
    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                pressed()
            }
        })
    }
}