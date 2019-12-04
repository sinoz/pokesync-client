package io.pokesync.core

import com.badlogic.gdx.utils.Disposable

/**
 * A screen.
 * @author Sino
 */
interface Screen : Disposable {
    /**
     * An adapter to [Screen] to allow implementations to only
     * override what they need.
     */
    abstract class Adapter : Screen {
        override fun show() {
            // overridable
        }

        override fun hide() {
            // overridable
        }

        override fun resume() {
            // overridable
        }

        override fun pause() {
            // overridable
        }

        override fun render(deltaTime: Float) {
            // overridable
        }

        override fun dispose() {
            // overridable
        }

        override fun resize(width: Int, height: Int) {
            // overridable
        }

        override fun exit() {
            // overridable
        }
    }

    /**
     * Called when the client is resumed.
     */
    fun resume()

    /**
     * Called when the client is put in a paused mode.
     */
    fun pause()

    /**
     * Presents this screen.
     */
    fun show()

    /**
     * Hides this screen.
     */
    fun hide()

    /**
     * Called when the user has attempted to exit the application.
     */
    fun exit()

    /**
     * Renders the screen.
     */
    fun render(deltaTime: Float)

    /**
     * Called when the application is resized.
     */
    fun resize(width: Int, height: Int)
}