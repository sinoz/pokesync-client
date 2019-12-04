package io.pokesync.core.game.ui.element.chatbox.command

/**
 * Listens for commands being entered by the user.
 * @author Sino
 */
interface CommandListener {
    /**
     * Handles the command.
     */
    fun handle(arguments: List<String>)
}