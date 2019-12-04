package io.pokesync.core.message

import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * An event of a user having entered invalid credentials.
 * @author Sino
 */
object UnableToFetchProfile : Message {
    /**
     * Creates a [PacketToMessageDecoder] to decode [UnableToFetchProfile]
     * messages.
     */
    fun decoder(): PacketToMessageDecoder<UnableToFetchProfile> =
        object : PacketToMessageDecoder<UnableToFetchProfile> {
            override fun decode(packet: Packet): UnableToFetchProfile =
                UnableToFetchProfile
        }
}