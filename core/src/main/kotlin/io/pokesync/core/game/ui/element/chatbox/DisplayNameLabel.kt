package io.pokesync.core.game.ui.element.chatbox

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.pokesync.core.game.model.DisplayName

/**
 * The display name label left of the input field.
 * @author Sino
 */
class DisplayNameLabel(skin: Skin) : Label(":", skin, "display-name") {
    /**
     * Returns whether a username is set.
     */
    fun isSet(): Boolean {
        return text.toString().length > 1
    }

    /**
     * Attaches a username to this label.
     */
    fun attachUsername(name: String) {
        setText("$name:")
    }

    /**
     * Returns the display name.
     */
    fun getDisplayName(): DisplayName {
        return DisplayName(text.toString())
    }
}