package io.pokesync.core

import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.PixmapLoader
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.pokesync.core.assets.*
import io.pokesync.core.assets.config.*
import io.pokesync.core.assets.loading.*
import io.pokesync.core.assets.texture.TextureList
import io.pokesync.core.assets.scene2d.FontStore
import io.pokesync.core.assets.scene2d.SkinStore
import io.pokesync.core.game.GameAssets
import io.pokesync.core.game.GameScreen
import io.pokesync.core.game.world.WorldGrid
import io.pokesync.core.login.LoginScreen
import io.pokesync.core.login.transition.GameTransitionListener
import io.pokesync.core.net.Client
import io.pokesync.core.net.MessageCodec
import io.pokesync.core.util.FrameProfiler
import io.pokesync.core.util.HeapProfiler
import io.pokesync.core.util.PreferenceSet
import io.pokesync.lib.gdx.ProgramCloseListener
import io.pokesync.rom.nitro.Archive
import ktx.async.KtxAsync
import io.pokesync.rom.nitro.Image as NitroImage

/**
 * The nexus of the client application that drives and connects everything.
 * This handler deals with the life cycle of all active screens and its
 * graphically drawn entities.
 * @author Sino
 */
class LifeCycleHandler : ApplicationAdapter(), ProgramCloseListener {
    private lateinit var input: InputMultiplexer

    private lateinit var screenStack: ScreenStack

    private lateinit var stage: Stage

    private lateinit var client: Client

    private lateinit var assets: AssetManager

    private lateinit var skinStore: SkinStore

    private lateinit var fontStore: FontStore

    private lateinit var profilingFont: BitmapFont

    private lateinit var frameProfiler: FrameProfiler

    private lateinit var heapProfiler: HeapProfiler

    private lateinit var preferenceSet: PreferenceSet

    override fun create() {
        assets = createAssetManager()
        screenStack = ScreenStack()

        fontStore = FontStore.create()
        skinStore = SkinStore.create(fontStore, assets)

        profilingFont = fontStore.get(FontStore.Type.WHITE_ROBOTO_MONO_PT11)!!

        preferenceSet = PreferenceSet()

        heapProfiler = HeapProfiler()
        frameProfiler = FrameProfiler()

        client = Client.create(MessageCodec.default())

        stage = createStage(ScreenViewport())

        input = createInputMultiplexer(stage)
        input.addProcessor(stage)

        initializeKtxAsync()
        setAsInputReceiver(input)

        val transitionListener = GameTransitionListener.impl(screenStack) {
            GameScreen(
                stage,
                input,
                skinStore,
                preferenceSet,
                GameAssets(assets),
                client
            )
        }
        val loginScreen = LoginScreen(stage, skinStore, assets, client, transitionListener)

        screenStack.push(loginScreen)

//        val gameScreen = GameScreen(stage, input, skinStore, preferenceSet, assets, client)
//
//        val displayName = DisplayName("Sino")
//        val position = MapPosition(0, 0, 60, 40)
//
//        gameScreen.attachChatboxDisplayName(displayName)
//        gameScreen.attachAvatar(PID(1), Gender.FEMALE, displayName, UserGroup.GAME_DEVELOPER, position)
//
//        screenStack.push(gameScreen)
    }

    /**
     * Initializes the KTX async library to allow the use of coroutines with LibGDX.
     */
    private fun initializeKtxAsync() {
        KtxAsync.initiate()
    }

    /**
     * Sets the given [InputProcessor] as the global receiver for all input given by the user.
     */
    private fun setAsInputReceiver(processor: InputProcessor) {
        Gdx.input.inputProcessor = processor
    }

    /**
     * Clears the screen by clearing the color and depth buffers.
     */
    private fun clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
    }

    /**
     * Draws a currently active screen, if there is one.
     */
    private fun renderActiveScreen() {
        screenStack.currentlyOnTop()?.render(Gdx.graphics.deltaTime)
    }

    /**
     * Breaths life into the [Stage], which enables user input.
     */
    private fun pulseStage() {
        stage.act(Gdx.graphics.deltaTime)
        stage.viewport.apply()
        stage.draw()
    }

    /**
     * Regulates the frame rate.
     */
    private fun regulateFPS() {
        frameProfiler.update()
    }

    /**
     * Draws the FPS.
     */
    private fun drawFPS() {
        stage.batch.begin()
        profilingFont.draw(stage.batch, "FPS: ${frameProfiler.getFrameRate()}", 3F, Gdx.graphics.height - 3F)
        stage.batch.end()
    }

    /**
     * Regulates the heap usage.
     */
    private fun regulateHeap() {
        heapProfiler.update()
    }

    /**
     * Draws the heap usage.
     */
    private fun drawHeapUsage() {
        stage.batch.begin()
        profilingFont.draw(
            stage.batch,
            "Heap: ${heapProfiler.getHeapInBytes() / (1024 * 1024)} MB",
            3F,
            Gdx.graphics.height - 23F
        )
        stage.batch.end()
    }

    /**
     * Disposes of resources consumed by all of the screens.
     */
    private fun disposeActivity() {
        while (!screenStack.isEmpty()) {
            screenStack.pop()?.dispose()
        }
    }

    /**
     * Disposes off the [Stage], clearing its used up resources back to memory.
     */
    private fun disposeStage() {
        stage.dispose()
    }

    /**
     * Disposes off the [AssetManager] and the [SkinStore], which has consumed resources.
     */
    private fun disposeAssets() {
        assets.dispose()
        fontStore.dispose()
        skinStore.dispose()
    }

    /**
     * Disposes off the [profilingFont], clearing its used up resources back to memory.
     */
    private fun disposeRegulationFont() {
        profilingFont.dispose()
    }

    /**
     * Constructs a new [Stage] that uses the given [Viewport].
     */
    private fun createStage(viewport: Viewport): Stage {
        return Stage(viewport)
    }

    /**
     * Constructs a new [InputMultiplexer] that adds the given set of [InputProcessor]s
     * in the defined order, to this multiplexer.
     */
    private fun createInputMultiplexer(vararg processors: InputProcessor): InputMultiplexer {
        val multiplexer = InputMultiplexer()
        for (proc in processors) {
            multiplexer.addProcessor(proc)
        }

        return multiplexer
    }

    /**
     * Constructs a new [AssetManager] with a set of asset loaders.
     */
    private fun createAssetManager(): AssetManager {
        val resolver = InternalFileHandleResolver()
        val manager = AssetManager(resolver, false)

        // register loaders for all of the standard gdx library facilities
        manager.setLoader(Pixmap::class.java, PixmapLoader(resolver))
        manager.setLoader(Skin::class.java, SkinLoader(resolver))
        manager.setLoader(Texture::class.java, TextureLoader(resolver))
        manager.setLoader(TextureAtlas::class.java, TextureAtlasLoader(resolver))

        // register loaders for the nitro images
        manager.setLoader(NitroImage::class.java, NitroImageLoader(resolver))
        manager.setLoader(Archive::class.java, ArchiveLoader(resolver))
        manager.setLoader(OverworldPlayerTexture::class.java, OverworldPlayerTextureLoader(resolver))
        manager.setLoader(OverworldNpcTexture::class.java, OverworldNpcTextureLoader(resolver))
        manager.setLoader(OverworldMonsterTexture::class.java, OverworldMonsterTextureLoader(resolver))
        manager.setLoader(ItemTexture::class.java, ItemTextureLoader(resolver))
        manager.setLoader(MonsterBattleTexture::class.java, MonsterBattleTextureLoader(resolver))
        manager.setLoader(TrainerBattleTexture::class.java, TrainerBattleTextureLoader(resolver))
        manager.setLoader(TextLabelBank::class.java, TextLabelBankLoader(resolver))

        // and also register loaders for all of the config files
        manager.setLoader(AnimSeqConfig::class.java, AnimationConfigLoader(resolver))
        manager.setLoader(MonsterConfig::class.java, MonsterConfigLoader(resolver))
        manager.setLoader(ItemConfig::class.java, ItemConfigLoader(resolver))
        manager.setLoader(NpcConfig::class.java, NpcConfigLoader(resolver))
        manager.setLoader(ObjectConfig::class.java, ObjectConfigLoader(resolver))
        manager.setLoader(WorldConfig::class.java, WorldConfigLoader(resolver))

        // and also register a loader for binary texture files
        manager.setLoader(TextureList::class.java, TextureListLoader(resolver))

        // register a loader for tile map related files
        manager.setLoader(WorldGrid::class.java, WorldGridLoader(resolver))

        return manager
    }

    override fun onAttempt(terminate: () -> Unit) {
        val activeScreen = screenStack.currentlyOnTop()
        if (activeScreen != null) {
            when (activeScreen) {
                is GameScreen -> activeScreen.exit()
                else -> terminate()
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        screenStack.currentlyOnTop()?.resize(width, height)
    }

    override fun render() {
        clearScreen()

        renderActiveScreen()
        pulseStage()

        if (preferenceSet.displayProfiling) {
            regulateFPS()
            regulateHeap()

            drawFPS()
            drawHeapUsage()
        }
    }

    override fun dispose() {
        disposeRegulationFont()
        disposeActivity()
        disposeStage()
        disposeAssets()
    }
}