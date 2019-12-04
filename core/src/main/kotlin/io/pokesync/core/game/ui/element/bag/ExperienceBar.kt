package io.pokesync.core.game.ui.element.bag

import com.badlogic.gdx.scenes.scene2d.ui.*
import ktx.style.get

/**
 * The experience bar of a pokemon.
 * @author Sino
 */
class ExperienceBar(skin: Skin) : Table(skin) {
    private var fillingCell: Cell<Image>

    init {
        background = skin["bar"]

        fillingCell = add(Image(skin, "experience-fill"))
            .left()
            .padLeft(1F)
            .padRight(1F)
            .growX()

        setPercentage(40)
    }

    /**
     * Sets the amount of experience in percentages the monster has.
     */
    fun setPercentage(percentage: Int) {
        val decimalStr = if (percentage >= 100) "1.0" else "0.${percentage}"

        fillingCell.width(prefWidth * decimalStr.toFloat())
    }
}