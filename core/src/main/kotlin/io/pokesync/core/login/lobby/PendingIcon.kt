package io.pokesync.core.login.lobby

import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as doAction
import ktx.style.get
import java.lang.IllegalStateException

/**
 * The animated icon on the top-right-corner of the lobby window.
 * @author Sino
 */
class PendingIcon(val skin: Skin) : Image(skin, "lobby-icon-1") {
    private var state = 0

    init {
        addAction(forever(sequence(delay(0.5F), doAction(::advanceToNextFrame))))
    }

    private fun advanceToNextFrame() {
        drawable = getNextFrame()

        state++
        if (state > 3) {
            state = 0
        }
    }

    private fun getNextFrame(): Drawable = when (state) {
        0 -> skin["lobby-icon-1"]
        1 -> skin["lobby-icon-2"]
        2 -> skin["lobby-icon-1"]
        3 -> skin["lobby-icon-3"]
        else -> throw IllegalStateException()
    }
}