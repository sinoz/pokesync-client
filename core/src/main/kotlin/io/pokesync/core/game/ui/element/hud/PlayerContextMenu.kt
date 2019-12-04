package io.pokesync.core.game.ui.element.hud

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * The right-click context menu drawn on a player when right-clicked on.
 * @author Sino
 */
class PlayerContextMenu(skin: Skin) : Table(skin) {
    /**
     * The position of the context menu on the screen.
     */
    val screenPoint = Vector3()

    // TODO id of player currently selected

    init {
        background = skin["popup"]
    }

    /**
     * Presents this menu and its elements.
     */
    fun show() {
        isVisible = true
    }

    /**
     * Hides this menu and its elements.
     */
    fun hide() {
        isVisible = false
    }

    /**
     * Adds a cancel button that closes this menu.
     */
    fun addCancelOption() {
        addOption("Cancel") {
            isVisible = false
        }
    }

    /**
     * Adds an option with the given label and action.
     */
    fun addOption(label: String, action: () -> Unit) {
        add(MenuOption(label, skin, action))
            .expandX()
            .fillX()
            .row()

        pack()
    }
}