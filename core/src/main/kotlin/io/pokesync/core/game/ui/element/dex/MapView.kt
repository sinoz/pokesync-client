package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * Presents a location of a monster on a map of a region.
 * @author Sino
 */
class MapView(skin: Skin) : Table(skin) {
    private val regionLabel = Label("", skin, "region-label")

    init {
        background = skin["region-map"]

        regionLabel.setPosition(7F, 170F)

        addActor(regionLabel)
    }

    /**
     * Sets the name of the region of the map to show.
     */
    fun setRegionName(value: String) {
        regionLabel.setText(value)
    }
}