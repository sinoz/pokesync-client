package io.pokesync.core.game.ui.element.hud

import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.pokesync.core.game.model.DisplayName
import ktx.style.get

/**
 * A user tag drawn on top of a user.
 * @author Sino
 */
class UserTag(val displayName: DisplayName, skin: Skin) : Table() {
    private val nameLabel = Label(displayName.str, skin, "user-tag")

    val screenPoint = Vector3(0F, 0F, 0F)

    val glyphLayout = GlyphLayout(nameLabel.style.font, displayName.str)

    init {
        background = skin["display-name-tag"]

        add(nameLabel)
        pack()
    }
}