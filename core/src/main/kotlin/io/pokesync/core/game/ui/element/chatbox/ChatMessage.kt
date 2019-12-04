package io.pokesync.core.game.ui.element.chatbox

import io.pokesync.core.game.model.DisplayName

/**
 * A chat message.
 * @author Sino
 */
data class ChatMessage(val sender: DisplayName, val payload: String)