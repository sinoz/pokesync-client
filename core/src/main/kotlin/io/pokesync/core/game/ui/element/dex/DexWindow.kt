package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.scenes.scene2d.ui.*
import io.pokesync.core.assets.BattleTextureAspect
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.core.game.ui.element.CloseWindowButton
import io.pokesync.core.game.ui.provider.MonsterProfileProvider
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.lib.gdx.onRenderingThread
import io.pokesync.lib.gdx.verticalSheetSlice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ktx.style.get

/**
 * The pokedex.
 * @author Sino
 */
class DexWindow(skin: Skin, gameSkin: Skin, val monsterProfileProvider: MonsterProfileProvider) :
    Window("", skin, "dex-window") {
    private val topPane =
        TopPane(skin, ::showMonsterLocationOnMap, ::playMonsterCry) { showInfoAboutMonster(currentSelectedMonster!!) }

    private val dexEntryTable = Table()

    private val separator = Image(skin, "separator")

    private val scrollPane = ButtonedScrollPane(dexEntryTable, gameSkin, "scroll-pane")

    private val closeButton = CloseWindowButton(gameSkin) {
        isVisible = false
        currentSelectedMonster = null

        topPane.closeInformationTable()
    }

    private var currentSelectedMonster: ModelId? = null

    private val namePopup = Container<Label>(Label("", skin, "entry-tooltip-label"))

    init {
        width = prefWidth
        height = prefHeight

        isResizable = false
        isMovable = true

        scrollPane.setForceScroll(false, true)
        scrollPane.setSmoothScrolling(false)
        scrollPane.setScrollBarPositions(false, true)

        titleTable
            .add(closeButton)
            .right()
            .padLeft(1F)
            .padBottom(1F)

        add(topPane)
            .expand()
            .fill()
            .top()
            .left()
            .row()

        add(scrollPane)
            .width(365F)
            .height(177F)
            .padTop(20F)
            .padBottom(10F)
            .bottom()

        addDexEntries()

        separator.setPosition(345F, 13F)
        separator.width = separator.prefWidth
        separator.height = 207F

        namePopup.isVisible = false
        namePopup.background = skin["popup"]

        namePopup.width = namePopup.prefWidth
        namePopup.height = namePopup.prefHeight

        addActor(separator)
        addActor(namePopup)
    }

    /**
     * Presents this monster dex.
     */
    fun show() {
        isVisible = true
    }

    /**
     * Hides this monster dex.
     */
    fun hide() {
        isVisible = false
    }

    /**
     * Shows the map for the user to pinpoint where a type of monster
     * may be located at.
     */
    private fun showMonsterLocationOnMap() {
        check(currentSelectedMonster != null)

        // TODO pass info
        topPane.showMonsterLocationOnMap("KANTO")
    }

    /**
     * Presents information about the specified monster.
     */
    private fun showInfoAboutMonster(id: ModelId) {
        currentSelectedMonster = id
        topPane.showInformationTable(
            monsterProfileProvider.provide(
                id,
                Gender.MALE,
                MonsterColoration.REGULAR,
                BattleTextureAspect.FRONT
            )
        )
    }

    /**
     * Plays a monster cry.
     */
    private fun playMonsterCry() {
        // TODO
    }

    /**
     * Adds all dex entries to the table.
     */
    private fun addDexEntries() {
        val colsPerRow = 5

        var rowId = 0
        var colId = 0

        for (i in 1 until ENTRY_COUNT) {
            val modelId = ModelId(i)

            val profile = monsterProfileProvider.provide(
                modelId,
                Gender.MALE,
                MonsterColoration.REGULAR,
                BattleTextureAspect.FRONT
            )

            val overworldTexture = profile.overworldTexture.underlying
            val overworldFrame = overworldTexture.verticalSheetSlice()[2]

            val dexEntry = DexEntry(modelId, skin, profile, overworldFrame, ::showInfoAboutMonster)
            val dexEntryCell = dexEntryTable.add(dexEntry)

            colId++
            if (colId >= colsPerRow) {
                dexEntryCell.row()

                colId = 0
                rowId++
            }
        }
    }

    companion object {
        const val ENTRY_COUNT = 152
    }
}