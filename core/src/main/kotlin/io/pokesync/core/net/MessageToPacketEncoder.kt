package io.pokesync.core.net

import io.pokesync.core.message.Message

/**
 * Translates [Message]s to [Packet]s.
 * @author Sino
 */
interface MessageToPacketEncoder<M : Message> {
    /**
     * Encodes the given [Message] of type [M] into a [Packet].
     */
    fun encode(message: M): Packet
}