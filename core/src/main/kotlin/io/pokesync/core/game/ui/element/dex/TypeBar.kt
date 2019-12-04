package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * Displays a type of a monster.
 * @author Sino
 */
class TypeBar(skin: Skin) : Table(skin) {
    private val label = Label("", skin, "types")

    init {
        background = skin["type"]

        add(label)
            .expand()
            .fill()
            .center()
            .padLeft(12F)
            .padBottom(8F)
    }

    fun setValue(value: String) {
        label.setText(value)
    }
}