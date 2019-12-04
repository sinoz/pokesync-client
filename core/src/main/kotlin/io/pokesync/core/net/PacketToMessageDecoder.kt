package io.pokesync.core.net

import io.pokesync.core.message.Message

/**
 * Translates [Packet]s to [Message]s.
 * @author Sino
 */
interface PacketToMessageDecoder<M : Message> {
    /**
     * Decodes the given [Packet] into a [Message] of type [M].
     */
    fun decode(packet: Packet): M
}