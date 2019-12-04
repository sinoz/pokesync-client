package io.pokesync.core.game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.pokesync.core.Screen
import io.pokesync.core.account.UserGroup
import io.pokesync.core.assets.scene2d.SkinStore
import io.pokesync.core.game.input.UserInteractionListener
import io.pokesync.core.game.input.WorldInputListener
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MapPosition
import io.pokesync.core.game.ui.element.BaseLayout
import io.pokesync.core.game.ui.element.chatbox.ChatboxWindow
import io.pokesync.core.game.ui.element.chatbox.command.*
import io.pokesync.core.game.ui.provider.BagItemProfileProvider
import io.pokesync.core.game.ui.provider.MonsterProfileProvider
import io.pokesync.core.game.ui.provider.PlayerSpriteProvider
import io.pokesync.core.game.world.EntityFactory
import io.pokesync.core.game.world.component.Networked
import io.pokesync.core.game.world.component.PID
import io.pokesync.core.game.world.message.DisconnectedFromServer
import io.pokesync.core.game.world.message.MessageListener
import io.pokesync.core.game.world.message.SendCommandAcrossWire
import io.pokesync.core.game.world.message.SingleThreadedMessageDispatcher
import io.pokesync.core.game.world.system.*
import io.pokesync.core.message.AttachFollower
import io.pokesync.core.message.MapRefreshed
import io.pokesync.core.message.SwitchPartySlots
import io.pokesync.core.net.CanConnectToRemote
import io.pokesync.core.util.PreferenceSet

/**
 * The screen that is to draw the game world and a user interface for the user
 * to interact with the world.
 * @author Sino
 */
class GameScreen(
    val stage: Stage,
    val input: InputMultiplexer,
    skins: SkinStore,
    val preferences: PreferenceSet,
    val gameAssets: GameAssets,
    val client: CanConnectToRemote
) : Screen.Adapter() {
    /**
     * The sprite batch used to draw the world.
     */
    private val worldBatch = SpriteBatch()

    /**
     * The orthographic camera that follows the user's avatar entity around.
     */
    private val camera: OrthographicCamera

    /**
     * The viewport for the game.
     */
    private val viewport: Viewport

    /**
     * The entity engine.
     */
    private val engine = Engine()

    /**
     * An intermediary system between all of the entity systems that delivers
     * messages to one another.
     */
    private val messageDispatcher = SingleThreadedMessageDispatcher()

    /**
     * The grid of tile maps that make up the world.
     */
    private val worldGrid = gameAssets.obtainWorldGrid()

    /**
     * The base layout of the game screen's user interface.
     */
    private val baseLayout: BaseLayout

    /**
     * A factory that produces entities.
     */
    private val entityFactory = EntityFactory(worldGrid, gameAssets)

    /**
     * The user's avatar [Entity].
     */
    private var avatar: Entity? = null

    /**
     * An [InputProcessor] that processes user input for the user to interact
     * with the user interface.
     */
    private lateinit var uiInput: InputProcessor

    /**
     * An [InputProcessor] that processes user input for the user's avatar to
     * interact with the world.
     */
    private lateinit var worldInput: InputProcessor

    /**
     * A [MessageListener] listening for [DisconnectedFromServer] messages.
     */
    private val serverDisconnectListener = object : MessageListener<DisconnectedFromServer> {
        override fun handle(c: DisconnectedFromServer) {
            switchToLoginScreen()
        }
    }

    /**
     * Initializes this screen.
     */
    init {
        // fetch all of the necessary skins for the user interface
        val dexSkin = skins.get(SkinStore.Type.MONSTER_DEX)!!
        val itemBagSkin = skins.get(SkinStore.Type.ITEM_BAG)!!
        val gameSkin = skins.get(SkinStore.Type.GAME)!!
        val pauseBackgroundSkin = skins.get(SkinStore.Type.PAUSE_BG)!!

        // create the providers that interface between the data layer and the ui layer
        val bagItemProfileProvider = BagItemProfileProvider.fromAssets(gameAssets)
        val monsterInfoProvider = MonsterProfileProvider.fromAssets(gameAssets)
        val playerSpriteProvider = PlayerSpriteProvider.fromAssets(gameAssets)

        // and create our base layout of the game's user interface and add it to the stage
        baseLayout = BaseLayout(
            itemBagSkin,
            dexSkin,
            gameSkin,
            pauseBackgroundSkin,
            playerSpriteProvider,
            monsterInfoProvider,
            bagItemProfileProvider
        )

        baseLayout.chatbox.addListener(ChatboxWindow.ChatListener.dispatched(messageDispatcher))
        baseLayout.chatbox.apply {
            switchToChannel(0)

            addCommandListener(GC, GarbageCollectListener())
            addCommandListener(COLFLAGS, RenderCollisionFlagsListener(preferences))
            addCommandListener(REGULATE, DisplayProfilingListener(preferences))
        }

        baseLayout.contextMenu.apply {
            addOption("Challenge", ::notifyPlayerOptionSelected)
            addOption("Trade", ::notifyPlayerOptionSelected)
            addOption("Befriend", ::notifyPlayerOptionSelected)
            addOption("Message", ::notifyPlayerOptionSelected)
            addOption("Block", ::notifyPlayerOptionSelected)
            addCancelOption()
        }

        stage.addActor(baseLayout)

        camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.zoom = 0.5F

        viewport = ScalingViewport(Scaling.none, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)
        viewport.update(Gdx.graphics.width, Gdx.graphics.height, false)

        // registers all of the necessary entity systems to the engine to process the game world
        engine.apply {
            addSystem(TeleportSystem())
            addSystem(WalkingSystem(worldGrid))
            addSystem(CyclingSystem(worldGrid))
            addSystem(JumpingSystem())
            addSystem(DoorSystem(camera))
            addSystem(NetworkSystem(messageDispatcher))
            addSystem(TrackingSystem(messageDispatcher, worldGrid, entityFactory))
            addSystem(CameraSystem(camera, messageDispatcher))
            addSystem(InputSystem(messageDispatcher))
            addSystem(HudSystem(messageDispatcher, camera, baseLayout))
            addSystem(PartySystem(messageDispatcher, baseLayout.party))
            addSystem(ChatSystem(baseLayout.chatbox, messageDispatcher))
            addSystem(RenderingSystem(messageDispatcher, worldBatch, camera, worldGrid, preferences))
        }

        // make all party slots draggable and drop targets. drag-and-dropping one slot
        // onto another, will trigger a slot switch action
        baseLayout.party.makeAllSlotsDragAndDropTargets { fromSlot, toSlot ->
            messageDispatcher.publish(SendCommandAcrossWire(SwitchPartySlots(fromSlot, toSlot)))
        }

        // dropping a monster from a party onto the world however, requests the
        // server to attach the monster as a follower onto the player
        baseLayout.party.addDropTarget(baseLayout) { slot ->
            messageDispatcher.publish(SendCommandAcrossWire(AttachFollower(slot)))
        }
    }

    /**
     * Shows this screen.
     */
    override fun show() {
        baseLayout.show()

        messageDispatcher.subscribe(DisconnectedFromServer::class.java, serverDisconnectListener)
    }

    /**
     * Hides this screen.
     */
    override fun hide() {
        input.removeProcessor(worldInput)
        input.removeProcessor(uiInput)

        baseLayout.hide()

        messageDispatcher.unsubscribe(DisconnectedFromServer::class.java, serverDisconnectListener)
    }

    /**
     * Called when the user has attempted to exit the application.
     */
    override fun exit() {
        baseLayout.showPauseBackground()
    }

    /**
     * Notifies the server that the user has selected an option on another
     * player's context menu.
     */
    private fun notifyPlayerOptionSelected() {
        // TODO
    }

    /**
     * Attaches the given [DisplayName] onto the [baseLayout.chatbox].
     */
    fun attachChatboxDisplayName(displayName: DisplayName) {
        baseLayout.chatbox.setDisplayName(displayName)
    }

    /**
     * Attaches an avatar for the user to control.
     */
    fun attachAvatar(pid: PID, gender: Gender, displayName: DisplayName, userGroup: UserGroup, position: MapPosition) {
        avatar = entityFactory
            .createAvatar(pid, gender, position, Direction.SOUTH, displayName, userGroup)
            .add(Networked(client))

        engine.addEntity(avatar)

        baseLayout.hud.assignCharacter(gender)

        uiInput = UserInteractionListener(camera, avatar!!, engine, baseLayout, messageDispatcher)
        worldInput = WorldInputListener(worldGrid, avatar!!, camera, baseLayout, messageDispatcher)

        input.apply {
            addProcessor(uiInput)
            addProcessor(worldInput)
        }

        messageDispatcher.publish(MapRefreshed(position.mapX, position.mapZ))
    }

    /**
     * Switches back to the login screen.
     */
    private fun switchToLoginScreen() {
        // TODO
    }

    /**
     * Updates all of the systems and entities within the engine.
     */
    private fun pulseEntityEngine(deltaTime: Float) {
        viewport.apply()
        engine.update(deltaTime)
    }

    override fun pause() {
        // TODO
    }

    override fun resume() {
        // TODO
    }

    override fun render(deltaTime: Float) {
        pulseEntityEngine(deltaTime)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        baseLayout.resize(width, height)
    }

    override fun dispose() {
        worldBatch.dispose()
    }
}