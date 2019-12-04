package io.pokesync.core.message

import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * An event of a user's account being disabled.
 * @author Sino
 */
object AccountDisabled : Message {
    /**
     * Creates a [PacketToMessageDecoder] to decode [AccountDisabled]
     * messages.
     */
    fun decoder(): PacketToMessageDecoder<AccountDisabled> =
        object : PacketToMessageDecoder<AccountDisabled> {
            override fun decode(packet: Packet): AccountDisabled =
                AccountDisabled
        }
}