package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.*
import io.pokesync.lib.gdx.horizontalSheetSlice

/**
 * The top part of the dex window.
 * @author Sino
 */
class TopPane(skin: Skin, pressedMapButton: () -> Unit, pressedCryButton: () -> Unit, pressedInfoButton: () -> Unit) :
    Table(skin) {
    private val informationTable = InformationTable(skin, pressedMapButton, pressedCryButton)

    private val mapView = MapView(skin)

    private val infoButton = InfoButton(skin, pressedInfoButton)

    init {
        informationTable.isVisible = false

        add(informationTable)
    }

    /**
     * Hides the information table.
     */
    fun closeInformationTable() {
        informationTable.isVisible = false
    }

    /**
     * Presents the information table.
     */
    fun showInformationTable(profile: MonsterProfile) {
        informationTable.isVisible = true

        informationTable.setMonsterName(profile.name)
        informationTable.setMonsterTitle(profile.title)
        informationTable.setMonsterDescription(profile.description)
        informationTable.setMonsterHeight(profile.height)
        informationTable.setMonsterWeight(profile.weight)
        informationTable.setPrimaryType(profile.primaryType)
        informationTable.setSecondaryType(profile.secondaryType)
        informationTable.attachMonsterSprite(profile.battleTexture.underlying.horizontalSheetSlice()[0])

        clearChildren()
        add(informationTable)
        pack()
    }

    /**
     * Presents the map with the monster's location on it.
     */
    fun showMonsterLocationOnMap(regionName: String) {
        mapView.setRegionName(regionName)

        clearChildren()
        add(mapView)
        add(infoButton).top().padRight(5F)
        pack()
    }
}