package io.pokesync.core.game.ui.element

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.pokesync.core.assets.scene2d.SkinStore
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.game.ui.element.bag.ItemBagWindow
import io.pokesync.core.game.ui.element.chatbox.ChatboxWindow
import io.pokesync.core.game.ui.element.dex.DexWindow
import io.pokesync.core.game.ui.element.donator.DonatorStoreButton
import io.pokesync.core.game.ui.element.hud.Dialogue
import io.pokesync.core.game.ui.element.hud.HudBar
import io.pokesync.core.game.ui.element.hud.PlayerContextMenu
import io.pokesync.core.game.ui.element.hud.UserTag
import io.pokesync.core.game.ui.element.party.PartyWindow
import io.pokesync.core.game.ui.element.pause.PauseBackground
import io.pokesync.core.game.ui.element.sidebar.SidebarTab
import io.pokesync.core.game.ui.provider.BagItemProfileProvider
import io.pokesync.core.game.ui.provider.MonsterProfileProvider
import io.pokesync.core.game.ui.provider.PlayerSpriteProvider

/**
 * The base layout of the ingame's user interface.
 * @author Sino
 */
class BaseLayout(
    val itemBagSkin: Skin,
    val dexSkin: Skin,
    val gameSkin: Skin,
    val pauseBackgroundSkin: Skin,

    playerSpriteProvider: PlayerSpriteProvider,
    monsterProfileProvider: MonsterProfileProvider,
    bagItemProfileProvider: BagItemProfileProvider
) : Table() {
    val sidebarTabs = createSidebarTabs()

    val chatbox = ChatboxWindow(gameSkin)

    val dex = DexWindow(dexSkin, gameSkin, monsterProfileProvider)

    val itemBag = ItemBagWindow(itemBagSkin, gameSkin, bagItemProfileProvider)

    val party = PartyWindow(gameSkin, monsterProfileProvider)

    val hud = HudBar(gameSkin, playerSpriteProvider)

    val donatorStoreButton = DonatorStoreButton(gameSkin) { } // TODO

    val dialogue = Dialogue(gameSkin)

    val contextMenu = PlayerContextMenu(gameSkin)

    val pauseBackground = PauseBackground(pauseBackgroundSkin, gameSkin)

    init {
        setFillParent(true)

        refreshPartyWindowPosition(Gdx.graphics.width, Gdx.graphics.height)
        refreshProgressHudPosition(Gdx.graphics.width, Gdx.graphics.height)
        refreshSidebarTabsPositions(Gdx.graphics.width, Gdx.graphics.height)
        refreshDexWindowPosition(Gdx.graphics.width, Gdx.graphics.height)
        refreshItemBagWindowPosition(Gdx.graphics.width, Gdx.graphics.height)
        refreshDonatorStoreButtonPosition(Gdx.graphics.width, Gdx.graphics.height)

        addActor(chatbox)
        addActor(party)
        addActor(hud)
        addActor(donatorStoreButton)
        addActor(dex)
        addActor(itemBag)
        addActor(dialogue)
        addActor(contextMenu)
        addActor(pauseBackground)

        pauseBackground.toBack()

        chatbox.toFront()
        party.toFront()
        hud.toFront()
        donatorStoreButton.toFront()
        dex.toFront()
        itemBag.toFront()
        contextMenu.toFront()

        pauseBackground.hide()
        dialogue.hide()
        contextMenu.hide()
        itemBag.hide()
        dex.hide()
    }

    /**
     * Presents this layout and all of its children.
     */
    fun show() {
        isVisible = true
    }

    /**
     * Hides this layout and all of its children.
     */
    fun hide() {
        isVisible = false
    }

    /**
     * Called when the screen has been resized.
     */
    fun resize(width: Int, height: Int) {
        refreshProgressHudPosition(width, height)
        refreshDonatorStoreButtonPosition(width, height)
        refreshSidebarTabsPositions(width, height)

        if (requiresRefresh(chatbox.x, chatbox.y, width, height)) {
            refreshChatboxPosition(width, height)
        }

        if (requiresRefresh(party.x, party.y, width, height)) {
            refreshPartyWindowPosition(width, height)
        }
    }

    /**
     * Toggles the visibility of the [baseLayout.pauseBackground].
     */
    fun togglePauseBackground() {
        if (pauseBackground.isVisible) {
            hidePauseBackground()
        } else {
            showPauseBackground()
        }
    }

    /**
     * Shows the pause background.
     */
    fun showPauseBackground() =
        with(pauseBackground) {
            show()
            toFront()
        }

    /**
     * Hides the pause background.
     */
    fun hidePauseBackground() =
        with(pauseBackground) {
            hide()
            toBack()
        }

    /**
     * Creates a new [UserTag] for the given [DisplayName].
     */
    fun createUserTag(displayName: DisplayName): UserTag =
        UserTag(displayName, gameSkin)

    /**
     * Constructs a [List] of [SidebarTab]s.
     */
    private fun createSidebarTabs(): List<SidebarTab> {
        val tabs = mutableListOf<SidebarTab>()
        val listeners = createSidebarTabListeners()

        for (type in SidebarTab.Type.values()) {
            val listener = listeners[type] ?: error("No callback to subscribe for type $type")
            val tab = SidebarTab(type, gameSkin) { listener() }

            tabs.add(tab)
        }

        return tabs
    }

    /**
     * Subscribes the sidebar tab listeners.
     */
    private fun createSidebarTabListeners(): Map<SidebarTab.Type, () -> Unit> {
        val listeners = mutableMapOf<SidebarTab.Type, () -> Unit>()

        listeners[SidebarTab.Type.CHATBOX] = { chatbox.show() }
        listeners[SidebarTab.Type.ITEM_BAG] = { itemBag.show() }
        listeners[SidebarTab.Type.SKILLS] = { }
        listeners[SidebarTab.Type.QUESTS] = { }
        listeners[SidebarTab.Type.ACHIEVEMENTS] = { }
        listeners[SidebarTab.Type.SETTINGS] = { }
        listeners[SidebarTab.Type.DEX] = { dex.show() }

        return listeners
    }

    /**
     * Sets the positions of each and every sidebar tab.
     */
    private fun refreshSidebarTabsPositions(screenWidth: Int, screenHeight: Int) {
        val offsetX = 0F
        val offsetY = screenHeight * 0.7F

        for (tab in sidebarTabs) {
            val tabId = tab.type.ordinal

            val tabX = offsetX
            val tabY = offsetY - (tabId * (tab.prefHeight + 10F))

            tab.setPosition(tabX, tabY)
            tab.setSize(tab.prefWidth, tab.prefHeight)

            addActor(tab)
        }
    }

    /**
     * Sets the party window position.
     */
    private fun refreshPartyWindowPosition(screenWidth: Int, screenHeight: Int) {
        party.setPosition(screenWidth - (party.prefWidth + 10F), (screenHeight / 2) - (party.prefHeight / 2))
    }

    /**
     * Sets the chatbox position.
     */
    private fun refreshChatboxPosition(screenWidth: Int, screenHeight: Int) {
        chatbox.setPosition(0F, 0F)
    }

    /**
     * Sets the dex window position.
     */
    private fun refreshDexWindowPosition(screenWidth: Int, screenHeight: Int) {
        dex.setPosition((screenWidth / 2) - (dex.prefWidth / 2), (screenHeight / 2) - (dex.prefHeight / 2))
    }

    /**
     * Sets the item bag window position.
     */
    private fun refreshItemBagWindowPosition(screenWidth: Int, screenHeight: Int) {
        itemBag.setPosition((screenWidth / 2) - (itemBag.prefWidth / 2), (screenHeight / 2) - (itemBag.prefHeight / 2))
    }

    /**
     * Refreshes the position of the [hud].
     */
    private fun refreshProgressHudPosition(screenWidth: Int, screenHeight: Int) {
        hud.setPosition(screenWidth - hud.prefWidth, screenHeight - hud.prefHeight)
    }

    /**
     * Refreshes the position of the [donatorStoreButton].
     */
    private fun refreshDonatorStoreButtonPosition(screenWidth: Int, screenHeight: Int) {
        donatorStoreButton.setPosition(
            screenWidth - hud.prefWidth - donatorStoreButton.prefWidth - 10F,
            screenHeight - donatorStoreButton.prefHeight
        )
    }

    /**
     * Checks if a refresh is required by comparing the X/Y coordinates.
     */
    private fun requiresRefresh(x: Float, y: Float, screenWidth: Int, screenHeight: Int): Boolean =
        x < 0 || x >= screenWidth || y < 0 || y >= screenHeight
}