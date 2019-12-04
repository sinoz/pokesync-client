package io.pokesync.core.login

import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.style.get
import kotlin.math.max
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as doAction

/**
 * The animated icon in the center of the login window.
 * @author Sino
 */
class LoadingIcon(val skin: Skin) : Image(skin, "loading-icon-1") {
    private var state = 1

    init {
        addAction(forever(sequence(delay(0.25F), doAction(::advanceToNextFrame))))
    }

    private fun advanceToNextFrame() {
        state = max(1, (state + 1) % 5)
        drawable = skin["loading-icon-$state"]
    }
}