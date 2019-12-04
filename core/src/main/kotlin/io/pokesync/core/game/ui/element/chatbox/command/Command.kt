package io.pokesync.core.game.ui.element.chatbox.command

/**
 * A chat command.
 * @author Sino
 */
data class Command(
    val trigger: String,
    val arguments: List<String>
)