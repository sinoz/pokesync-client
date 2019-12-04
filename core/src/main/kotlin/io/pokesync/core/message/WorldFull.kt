package io.pokesync.core.message

import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * An event of the game server being full.
 * @author Sino
 */
object WorldFull : Message {
    /**
     * Creates a [PacketToMessageDecoder] to decode [WorldFull]
     * messages.
     */
    fun decoder(): PacketToMessageDecoder<WorldFull> =
        object : PacketToMessageDecoder<WorldFull> {
            override fun decode(packet: Packet): WorldFull =
                WorldFull
        }
}