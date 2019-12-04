package io.pokesync.core.login.lobby

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * The username label left of the input field.
 * @author Sino
 */
class UsernameLabel(skin: Skin) : Label(":", skin, "display-name") {
    /**
     * Attaches a username to this label.
     */
    fun attachUsername(name: String) {
        setText("$name:")
    }
}