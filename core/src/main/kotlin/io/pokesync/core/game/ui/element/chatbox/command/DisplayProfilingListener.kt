package io.pokesync.core.game.ui.element.chatbox.command

import io.pokesync.core.util.PreferenceSet

/**
 * Toggles the top-left display of profiling such as FPS and Heap Usage.
 * @author Sino
 */
class DisplayProfilingListener(val pref: PreferenceSet) : CommandListener {
    override fun handle(arguments: List<String>) {
        pref.displayProfiling = !pref.displayProfiling
    }
}