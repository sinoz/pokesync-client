package io.pokesync.core.game.ui.element.chatbox.command

/**
 * A repository of [CommandListener]s.
 * @author Sino
 */
class CommandListenerRepository {
    private val listeners = mutableMapOf<String, CommandListener>()

    /**
     * Subscribes a [CommandListener] to the specified trigger.
     */
    fun subscribe(trigger: String, listener: CommandListener) {
        listeners[trigger] = listener
    }

    /**
     * Unsubscribes any [CommandListener] from the given trigger.
     */
    fun unsubscribe(trigger: String) {
        listeners.remove(trigger)
    }

    /**
     * Looks up a [CommandListener].
     */
    fun get(trigger: String): CommandListener? =
        listeners[trigger]
}