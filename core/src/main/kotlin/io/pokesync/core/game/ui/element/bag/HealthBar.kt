package io.pokesync.core.game.ui.element.bag

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * The health bar of a pokemon.
 * @author Sino
 */
class HealthBar(skin: Skin) : Table(skin) {
    private var fillingCell: Cell<Image>

    init {
        background = skin["bar"]

        fillingCell = add(Image(skin, "health-fill"))
            .left()
            .padLeft(1F)
            .padRight(1F)
            .growX()
    }

    /**
     * Sets the amount of health in percentages the monster has.
     */
    fun setPercentage(percentage: Int) {
        val decimalStr = if (percentage >= 100) "1.0" else "0.${percentage}"

        fillingCell.width(prefWidth * decimalStr.toFloat())
    }
}