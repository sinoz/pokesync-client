package io.pokesync.desktop

import io.pokesync.core.LifeCycleHandler
import io.pokesync.desktop.DesktopApplication.Companion.Config.Companion.desktop

/**
 * The default configuration for desktop.
 */
val DefaultDesktopConfig = desktop {
    title("PokeSync")

    desktopDisplayModeWidth()
    desktopDisplayModeHeight()

    icon("resources/icons/16x16.png")
    icon("resources/icons/32x32.png")

    allowSoftwareMode(true)
    resizable(true)
}

/**
 * The main entry point to the PokeSync client application, for on the desktop.
 */
fun main() {
    DesktopApplication(LifeCycleHandler(), DefaultDesktopConfig)
}