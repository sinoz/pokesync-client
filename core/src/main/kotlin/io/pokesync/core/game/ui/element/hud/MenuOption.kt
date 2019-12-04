package io.pokesync.core.game.ui.element.hud

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

/**
 * A menu option.
 * @author Sino
 */
class MenuOption(label: String, skin: Skin, pressed: () -> Unit) : TextButton(label, skin, "context-menu-option") {
    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                pressed()
            }
        })
    }
}