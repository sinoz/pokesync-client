package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * Renders information about a selected monster.
 * @author Sino
 */
class InformationTable(skin: Skin, pressedMapButton: () -> Unit, pressedCryButton: () -> Unit) : Table(skin) {
    /**
     * The 'INFO' display.
     */
    private val info = Container(Label("INFO", skin, "info-text"))

    /**
     * Displays the name and a short description of the monster.
     */
    private val nameDisplay = NameBar(skin)

    /**
     * Draws the monster sprite itself.
     */
    private val monsterView = MonsterView(skin)

    /**
     * Displays the monster's primary typing.
     */
    private val primaryType = TypeBar(skin)

    /**
     * Displays the monster's secondary typing.
     */
    private val secondaryType = TypeBar(skin)

    /**
     * Draws the height and the weight of the monster.
     */
    private val heightWeight = HeightWeightBar(skin)

    /**
     * Displays a more elaborate description of the monster.
     */
    private val descriptionBox = DescriptionBox(skin)

    /**
     * The map button.
     */
    private val mapButton = MapButton(skin, pressedMapButton)

    /**
     * The cry button.
     */
    private val cryButton = CryButton(skin, pressedCryButton)

    init {
        info.background = skin["info"]

        add(createLeftColumn()).top().left()
        add(createCenterColumn()).top().left()
        add(createRightColumn()).top().right().row()
        add(createBottomColumn()).bottom().left().colspan(3)

        pack()
    }

    fun attachMonsterSprite(region: TextureRegion) {
        monsterView.setMonsterSprite(region)
    }

    fun setMonsterName(value: String) {
        nameDisplay.setMonsterName(value)
    }

    fun setMonsterTitle(value: String) {
        nameDisplay.setTitle(value)
    }

    fun setMonsterDescription(value: String) {
        descriptionBox.setDescription(value)
    }

    fun setMonsterWeight(value: Int) {
        heightWeight.setWeight(value)
    }

    fun setMonsterHeight(value: Int) {
        heightWeight.setHeight(value)
    }

    fun setPrimaryType(value: String) {
        primaryType.setValue(value)
    }

    fun setSecondaryType(value: String) {
        secondaryType.setValue(value)
    }

    private fun createLeftColumn(): Table {
        val table = Table()

        table.add(info).padBottom(4F).row()
        table.add(monsterView).padBottom(6F).row()
        table.pack()

        return table
    }

    private fun createCenterColumn(): Table {
        val table = Table()

        table.add(nameDisplay).colspan(2).row()
        table.add(primaryType)
        table.add(secondaryType).padRight(2F).row()
        table.add(heightWeight).colspan(2).row()
        table.pack()

        return table
    }

    private fun createRightColumn(): Table {
        val table = Table()

        table.add(mapButton).padRight(5F).row()
        table.add(cryButton).padTop(5F).padRight(5F)
        table.pack()

        return table
    }

    private fun createBottomColumn(): Table {
        val table = Table()

        table.add(descriptionBox)

        return table
    }
}