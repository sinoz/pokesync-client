package io.pokesync.core.login

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import io.ktor.util.error
import io.pokesync.core.Screen
import io.pokesync.core.account.Email
import io.pokesync.core.account.Password
import io.pokesync.core.assets.config.*
import io.pokesync.core.assets.loading.*
import io.pokesync.core.assets.scene2d.SkinStore
import io.pokesync.core.assets.texture.ShadowTexture
import io.pokesync.core.assets.texture.TextureList
import io.pokesync.core.client.BuildNumber
import io.pokesync.core.game.world.WorldGrid
import io.pokesync.core.login.lobby.LobbyWindow
import io.pokesync.core.login.transition.GameTransitionListener
import io.pokesync.core.message.*
import io.pokesync.core.net.CanConnectToRemote
import io.pokesync.core.net.ConnectResponse
import io.pokesync.core.net.RemoteEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import ktx.async.onRenderingThread
import mu.KotlinLogging
import java.nio.file.Paths
import java.time.Duration
import io.pokesync.rom.nitro.Image as NitroImage

/**
 * The login screen that allows the user to log into the game.
 * @author Sino
 */
class LoginScreen(
    val stage: Stage,
    val skinStore: SkinStore,
    val assets: AssetManager,
    val client: CanConnectToRemote,
    val transitionListener: GameTransitionListener
) : Screen.Adapter() {
//        lobbyWindow.usernameLabel.attachUsername("Sino")
//
//        lobbyWindow.queueState.setCurrentSpot(82)
//        lobbyWindow.queueState.setQueueSize(627)
//
//        loginBackground.showLobby(lobbyWindow)

    /**
     * Used to log information to console and/or to file.
     */
    private val log = KotlinLogging.logger {}

    /**
     * The [Skin] of the login background.
     */
    private val loginBgSkin = skinStore.get(SkinStore.Type.LOGIN_BG)!!

    /**
     * The [Skin] of all of the login related UI elements.
     */
    private val loginSkin = skinStore.get(SkinStore.Type.LOGIN_ELEMENTS)!!

    /**
     * The loading icon.
     */
    private val loadingIcon = LoadingIcon(loginSkin)

    /**
     * The loading text.
     */
    private val loadingText = LoadingText(loginSkin)

    /**
     * The login background itself.
     */
    private val loginBackground = LoginBackground(loginBgSkin)

    /**
     * The window of the lobby where users may chat with each other while they
     * wait for entrance into the game.
     */
    private val lobbyWindow = LobbyWindow(loginSkin)

    /**
     * A modal containing the input fields to login.
     */
    private val inputModal = InputModal(loginSkin, ::extractFieldsAndAttemptLogin)

    /**
     * The exit button for users to press when they desire to close
     * the application.
     */
    private val exitButton = ExitButton(loginSkin, ::openExitDialog)

    /**
     * The login button for users to press when they desire to log
     * into a game server.
     */
    private val loginButton = LoginButton(loginSkin, ::extractFieldsAndAttemptLogin)

    /**
     * Displays a login failure.
     */
    private val failureDisplay = FailureDisplay(loginSkin)

    /**
     * Indicates whether this screen has finished loading assets.
     */
    private var finishedLoading = false

    init {
        stage.addActor(loginBackground)
    }

    /**
     * A bridge method to extract the input fields and invokes [attemptLogin]
     * inside a global-level coroutine.
     */
    private fun extractFieldsAndAttemptLogin() {
        // extract the user's input
        val email = Email(inputModal.emailField.text)
        val password = Password(inputModal.passwordField.text)

        // show the loading icon to present a busy application
        loginBackground.showLoadingIcon(loadingIcon, loadingText)

        // and attempt to log into the game
        GlobalScope.launch(Dispatchers.IO) {
            attemptLogin(email, password)
        }
    }

    /**
     * Attempts to connect to the remote game service endpoint, sending a
     * login request with the given user credentials, once connected.
     */
    private suspend fun attemptLogin(email: Email, password: Password) {
        val connectResponse = connectToGameService()
        if (connectResponse != ConnectResponse.Ok) {
            onRenderingThread {
                rejectLogin(ResponseType.Bad.UNABLE_TO_CONNECT)
            }

            if (connectResponse is ConnectResponse.Otherwise) {
                log.error(connectResponse.error)
            }

            return
        }

        val request = makeLoginRequest(email, password)
        val result = withTimeoutOrNull(LOGIN_TIMEOUT.toMillis()) { query(request) } ?: ResponseType.Bad.TIMED_OUT

        when (result) {
            is ResponseType.Ok -> {
                transitionListener.transition(
                    result.message.pid,
                    result.message.gender,
                    result.message.displayName,
                    result.message.userGroup,
                    result.message.position
                )
            }

            is ResponseType.Bad -> {
                onRenderingThread {
                    rejectLogin(result)
                }
            }
        }
    }

    /**
     * Queries the server with the given [RequestLogin] command and waits for
     * a response from the server, returning it in the form of a [ResponseType].
     */
    private suspend fun query(request: RequestLogin): ResponseType {
        client.send(request, immediateFlush = true)

        return when (val message = client.receive()) {
            is LoginSuccess -> ResponseType.Ok(message)
            else -> MESSAGE_TO_RESPONSES[message] ?: ResponseType.Bad.UNEXPECTED_RESPONSE
        }
    }

    /**
     * Creates a [RequestLogin] command out of the given user credentials.
     */
    private fun makeLoginRequest(email: Email, password: Password) =
        RequestLogin(BuildNumber(major = 0, minor = 1, patch = 0), email, password)

    /**
     * Attempts to connect to the game service.
     */
    private suspend fun connectToGameService(): ConnectResponse =
        client.connect(RemoteEndpoint.GAME_SERVICE, LOGIN_TIMEOUT.toMillis())

    /**
     * Rejects the user's attempt to log into the game.
     */
    private fun rejectLogin(responseType: ResponseType.Bad) {
        loginBackground.showLoginBox(inputModal, loginButton, exitButton, failureDisplay)

        failureDisplay.setResponseText(responseType.message)
        failureDisplay.show()
    }

    /**
     * Opens up a dialog to give the user the option to close the application.
     */
    private fun openExitDialog() {
        // TODO
    }

    override fun show() {
        loginBackground.isVisible = true
        loginBackground.showLoadingIcon(loadingIcon, loadingText)

        if (!assets.isLoaded(NitroImage::class.java.simpleName)) {
            assets.load(
                NitroImage::class.java.simpleName,
                NitroImage::class.java,
                NitroImageParameter(Paths.get("resources/roms/images/"))
            )
        }

        if (!assets.isLoaded(AnimSeqConfig::class.java.simpleName)) {
            assets.load(
                AnimSeqConfig::class.java.simpleName,
                AnimSeqConfig::class.java,
                AnimationConfigParameter(Paths.get("resources/data/config/anim_seq.dat"))
            )
        }

        if (!assets.isLoaded(MonsterConfig::class.java.simpleName)) {
            assets.load(
                MonsterConfig::class.java.simpleName,
                MonsterConfig::class.java,
                MonsterConfigLoader.Parameters(Paths.get("resources/data/config/monsters.dat"))
            )
        }

        if (!assets.isLoaded(NpcConfig::class.java.simpleName)) {
            assets.load(
                NpcConfig::class.java.simpleName,
                NpcConfig::class.java,
                NpcConfigLoader.Parameters(Paths.get("resources/data/config/npcs.dat"))
            )
        }

        if (!assets.isLoaded(ObjectConfig::class.java.simpleName)) {
            assets.load(
                ObjectConfig::class.java.simpleName,
                ObjectConfig::class.java,
                ObjectConfigLoader.Parameters(Paths.get("resources/data/config/objects.dat"))
            )
        }

        if (!assets.isLoaded(ItemConfig::class.java.simpleName)) {
            assets.load(
                ItemConfig::class.java.simpleName,
                ItemConfig::class.java,
                ItemConfigParameter(Paths.get("resources/data/config/items.dat"))
            )
        }

        if (!assets.isLoaded(WorldConfig::class.java.simpleName)) {
            assets.load(
                WorldConfig::class.java.simpleName,
                WorldConfig::class.java,
                WorldConfigLoader.Parameters(Paths.get("resources/data/config/world.dat"))
            )
        }

        if (!assets.isLoaded(WorldGrid::class.java.simpleName)) {
            assets.load(WorldGrid::class.java.simpleName, WorldGrid::class.java)
        }

        if (!assets.isLoaded(ShadowTexture::class.java.simpleName)) {
            assets.load(
                ShadowTexture::class.java.simpleName,
                TextureList::class.java,
                TextureListLoader.Parameters(Paths.get("resources/data/texture/0.dat"))
            )
        }
    }

    override fun hide() {
        loginBackground.isVisible = false
    }

    override fun render(deltaTime: Float) {
        if (assets.update()) {
            if (!finishedLoading) {
                loginBackground.showLoginBox(inputModal, loginButton, exitButton, failureDisplay)
                finishedLoading = true
            }
        }
    }

    companion object {
        /**
         * The login timeout in milliseconds.
         */
        val LOGIN_TIMEOUT = Duration.ofSeconds(15)!!

        /**
         * A mapping between [Message] values and [ResponseType]s.
         */
        val MESSAGE_TO_RESPONSES = mapOf(
            Pair(InvalidCredentials, ResponseType.Bad.INVALID_CREDENTIALS),
            Pair(WorldFull, ResponseType.Bad.WORLD_FULL),
            Pair(AlreadyLoggedIn, ResponseType.Bad.ALREADY_LOGGED_IN),
            Pair(AccountDisabled, ResponseType.Bad.ACCOUNT_DISABLED),
            Pair(UnableToFetchProfile, ResponseType.Bad.UNABLE_TO_FETCH_PROFILE),
            Pair(AuthenticationTimedOut, ResponseType.Bad.TIMED_OUT)
        )
    }
}