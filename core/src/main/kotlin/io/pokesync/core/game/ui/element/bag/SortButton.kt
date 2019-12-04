package io.pokesync.core.game.ui.element.bag

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.pokesync.core.game.ui.element.bag.SortingType.*
import ktx.style.get

/**
 * A button to sort displayed items in different ways.
 * @author Sino
 */
class SortButton(skin: Skin, pressed: (SortingType) -> Unit) : Button(skin, "sort-alphanumeric-neutral") {
    private var sortType = NEUTRAL

    init {
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                advanceSortingType()

                style = when (sortType) {
                    NEUTRAL -> skin["sort-alphanumeric-neutral"]
                    ALPHA_NUMERIC_ASCENDING -> skin["sort-alphanumeric-ascending"]
                    ALPHA_NUMERIC_DESCENDING -> skin["sort-alphanumeric-descending"]
                }

                pressed(sortType)

                return true
            }
        })
    }

    private fun advanceSortingType() {
        sortType = when (sortType) {
            NEUTRAL -> ALPHA_NUMERIC_ASCENDING
            ALPHA_NUMERIC_ASCENDING -> ALPHA_NUMERIC_DESCENDING
            ALPHA_NUMERIC_DESCENDING -> NEUTRAL
        }
    }
}