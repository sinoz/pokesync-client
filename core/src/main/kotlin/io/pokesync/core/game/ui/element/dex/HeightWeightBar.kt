package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * Renders the height and the weight of the monster.
 * @author Sino
 */
class HeightWeightBar(skin: Skin) : Table(skin) {
    private val heightLabel = Label("", skin, "height")

    private val weightLabel = Label("", skin, "weight")

    init {
        background = skin["height-weight"]

        add(heightLabel).grow().left().padLeft(40F).row()
        add(weightLabel).grow().left().padLeft(40F).padBottom(8F)
    }

    fun setHeight(value: Int) {
        heightLabel.setText("${value / 10F} m")
    }

    fun setWeight(value: Int) {
        weightLabel.setText("${value / 10F} kg")
    }
}