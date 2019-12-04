package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * Contains a description of the monster.
 * @author Sino
 */
class DescriptionBox(skin: Skin) : Table(skin) {
    private val label = Label("", skin, "description")

    init {
        background = skin["description-box"]

        label.setWrap(true)

        add(label)
            .expand()
            .fill(0.95F, 1F)
            .left()
            .top()
            .padLeft(6F)
    }

    fun setDescription(value: String) {
        label.setText(value)
    }
}