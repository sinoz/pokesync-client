package io.pokesync.core.message

import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * An event of a user's attempt to be authenticated or logged in
 * having timed out.
 * @author Sino
 */
object AuthenticationTimedOut : Message {
    /**
     * Creates a [PacketToMessageDecoder] to decode [AuthenticationTimedOut]
     * messages.
     */
    fun decoder(): PacketToMessageDecoder<AuthenticationTimedOut> =
        object : PacketToMessageDecoder<AuthenticationTimedOut> {
            override fun decode(packet: Packet): AuthenticationTimedOut =
                AuthenticationTimedOut
        }
}