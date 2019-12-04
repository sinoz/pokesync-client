package io.pokesync.core.game.ui.element.chatbox.command

import io.pokesync.core.util.PreferenceSet

/**
 * A [CommandListener] that flags the collision flags rendering flag.
 * @author Sino
 */
class RenderCollisionFlagsListener(val pref: PreferenceSet) : CommandListener {
    override fun handle(arguments: List<String>) {
        pref.renderCollisionFlags = !pref.renderCollisionFlags
    }
}