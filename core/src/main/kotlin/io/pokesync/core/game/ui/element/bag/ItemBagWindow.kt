package io.pokesync.core.game.ui.element.bag

import com.badlogic.gdx.scenes.scene2d.ui.*
import io.pokesync.core.game.ui.element.CloseWindowButton
import io.pokesync.core.game.ui.provider.BagItemProfileProvider

/**
 * The player's bag full of items.
 * @author Sino
 */
class ItemBagWindow(skin: Skin, gameSkin: Skin, bagItemProfileProvider: BagItemProfileProvider) :
    Window("", skin, "item-bag-window") {
    private val pocketSwitchAction = { type: PocketType ->
        switchToPocket(type)
    }

    private val partySelectionView = PartySelectionView(skin, skin)

    private val pocketButtons = createPocketButtons()

    private val sortButton = SortButton(skin, ::resortItems)

    private val itemView = ItemView(pocketButtons, skin, bagItemProfileProvider)

    private val separator = Image(skin, "separator")

    private val scrollPane = ButtonedScrollPane(itemView, gameSkin, "scroll-pane")

    private val closeButton = CloseWindowButton(gameSkin) {
        isVisible = false
    }

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
            .padRight(4F)

        addActor(partySelectionView)
        addActor(createButtonRow())
        addActor(scrollPane)
        addActor(separator)

        scrollPane.setPosition(5F, 22F)
        scrollPane.width = 450F
        scrollPane.height = 140F

        separator.setPosition(417F, 13F)
        separator.width = separator.prefWidth
        separator.height = 160F

        partySelectionView.setPosition(6F, 221F)
        partySelectionView.isVisible = false

        for (i in 0 until 100) {
            pocketButtons[PocketType.MONSTER_BALLS]!!.add(Item(3, 1))
        }
    }

    /**
     * Re-sorts all of the items within the item bag.
     */
    private fun resortItems(sortingType: SortingType) {
        // TODO
    }

    /**
     * Switches the [itemView] to display the items that are located
     * in the specified [PocketType].
     */
    fun switchToPocket(type: PocketType) {
        itemView.switchToPocket(type)
    }

    /**
     * Presents this item bag.
     */
    fun show() {
        isVisible = true
    }

    /**
     * Hides this item bag.
     */
    fun hide() {
        isVisible = false
    }

    /**
     * Creates a [Table] with all of the [pocketButtons]s and the [sortButton].
     */
    private fun createButtonRow(): Table {
        val table = Table()
        table.setPosition(234F, 197F)

        for (pocketType in PocketType.values()) {
            table
                .add(pocketButtons[pocketType])
                .padRight(2F)
        }

        table
            .add(sortButton)
            .padLeft(4F)

        return table
    }

    /**
     * Constructs a collection of [Pocket] mapped by their [PocketType]s.
     */
    private fun createPocketButtons(): Map<PocketType, Pocket> {
        val pockets = mutableMapOf<PocketType, Pocket>()
        for (pocketType in PocketType.values()) {
            pockets[pocketType] = Pocket(pocketType, skin, pocketSwitchAction)
        }

        return pockets
    }
}