package io.pokesync.core.message

import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * An event of a user having entered invalid credentials.
 * @author Sino
 */
object InvalidCredentials : Message {
    /**
     * Creates a [PacketToMessageDecoder] to decode [InvalidCredentials]
     * messages.
     */
    fun decoder(): PacketToMessageDecoder<InvalidCredentials> =
        object : PacketToMessageDecoder<InvalidCredentials> {
            override fun decode(packet: Packet): InvalidCredentials =
                InvalidCredentials
        }
}