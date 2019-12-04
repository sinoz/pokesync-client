package io.pokesync.core.game.ui.element.pause

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * The background for the pause menu.
 * @author Sino
 */
class PauseBackground(backgroundSkin: Skin, elementsSkin: Skin) : Table(backgroundSkin) {
    init {
        background = backgroundSkin["default"]

        setFillParent(true)
    }

    /**
     * Presents this background and its elements.
     */
    fun show() {
        isVisible = true
    }

    /**
     * Hides this background and its elements.
     */
    fun hide() {
        isVisible = false
    }
}