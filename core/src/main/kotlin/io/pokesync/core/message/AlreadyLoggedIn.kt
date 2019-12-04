package io.pokesync.core.message

import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * An event of a user's account already being logged in.
 * @author Sino
 */
object AlreadyLoggedIn : Message {
    /**
     * Creates a [PacketToMessageDecoder] to decode [AlreadyLoggedIn]
     * messages.
     */
    fun decoder(): PacketToMessageDecoder<AlreadyLoggedIn> =
        object : PacketToMessageDecoder<AlreadyLoggedIn> {
            override fun decode(packet: Packet): AlreadyLoggedIn =
                AlreadyLoggedIn
        }

}