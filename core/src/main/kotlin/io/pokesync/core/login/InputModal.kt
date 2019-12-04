package io.pokesync.core.login

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * A modal that allows the user to enter their account credentials and
 * attempt to log into the game.
 * @author Sino
 */
class InputModal(skin: Skin, pressedEnter: () -> Unit) : Table(skin) {
    val emailFieldLabel = Table(skin)
    val passwordFieldLabel = Table(skin)

    val emailField = EmailField(skin)
    val passwordField = PasswordField(skin, pressedEnter)

    init {
        setBackground("login-box")

        emailFieldLabel.background = skin["auth-box"]
        emailFieldLabel.add(Label("Email", skin, "email-field-label")).expandX().left().padLeft(10F)

        passwordFieldLabel.background = skin["auth-box"]
        passwordFieldLabel.add(Label("Password", skin, "password-field-label")).expandX().left().padLeft(10F)

        add(emailFieldLabel).left()
        add(emailField).right().row()

        add(passwordFieldLabel).left()
        add(passwordField).right().row()
    }
}