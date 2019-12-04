package io.pokesync.desktop

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import io.pokesync.lib.gdx.ProgramCloseListener

/**
 * A [LwjglApplication] that is aimed to target a user's desktop environment.
 * @author Sino
 */
class DesktopApplication<T>(val l: T, config: Config) : LwjglApplication(l, config.underlying)
        where T : ApplicationListener,
              T : ProgramCloseListener {
    companion object {
        class Config private constructor(val underlying: LwjglApplicationConfiguration) {
            companion object {
                fun desktop(init: Config.() -> Unit): Config {
                    val bldr = Config(LwjglApplicationConfiguration())
                    bldr.init()
                    return bldr
                }
            }

            fun width(width: Int) {
                underlying.width = width
            }

            fun height(height: Int) {
                underlying.height = height
            }

            fun desktopDisplayModeWidth() {
                width(LwjglApplicationConfiguration.getDesktopDisplayMode().width)
            }

            fun desktopDisplayModeHeight() {
                height(LwjglApplicationConfiguration.getDesktopDisplayMode().height)
            }

            fun icon(path: String) {
                underlying.addIcon(path, Files.FileType.Internal)
            }

            fun title(title: String) {
                underlying.title = title
            }

            fun resizable(resizable: Boolean) {
                underlying.resizable = resizable
            }

            fun pauseWhenBackground(paused: Boolean) {
                underlying.pauseWhenBackground = paused
            }

            fun pauseWhenMinimized(paused: Boolean) {
                underlying.pauseWhenMinimized = paused
            }

            fun allowSoftwareMode(allowed: Boolean) {
                underlying.allowSoftwareMode = allowed
            }

            fun vSyncEnabled(enabled: Boolean) {
                underlying.vSyncEnabled = enabled
            }
        }
    }

    override fun exit() {
        l.onAttempt { super.exit() }
    }
}