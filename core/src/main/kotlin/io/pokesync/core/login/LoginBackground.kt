package io.pokesync.core.login

import com.badlogic.gdx.scenes.scene2d.ui.*
import io.pokesync.core.login.lobby.LobbyWindow

/**
 * The login background on which all user interface elements of the
 * login screen are placed on.
 * @author Sino
 */
class LoginBackground(skin: Skin) : Table(skin) {
    init {
        setFillParent(true)
        setBackground("default")
    }

    /**
     * Places the loading text on this background. All other children
     * are removed.
     */
    fun showLoadingIcon(loadingIcon: Image, text: Label) {
        clearChildren()

        add(loadingIcon).center().row()
        add(text).padTop(10F).center()
    }

    /**
     * Shows the lobby window. All other children are removed.
     */
    fun showLobby(window: LobbyWindow) {
        clearChildren()
        add(window)
    }

    /**
     * Shows the login input box and the related login/exit buttons.
     * All other children are removed.
     */
    fun showLoginBox(table: Table, loginButton: Button, exitButton: Button, failureDisplay: Table) {
        clearChildren()

        add(table)
            .colspan(2)
            .padBottom(20F)
            .row()

        add(exitButton)
        add(loginButton).row()

        add(failureDisplay)
            .colspan(2)
            .center()
            .padTop(25F)
    }
}