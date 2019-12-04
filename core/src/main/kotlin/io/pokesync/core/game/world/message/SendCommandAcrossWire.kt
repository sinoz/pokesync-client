package io.pokesync.core.game.world.message

import io.pokesync.core.message.Message

/**
 * A command to send a command as a payload across the wire to the server.
 * @author Sino
 */
data class SendCommandAcrossWire(val payload: Message)