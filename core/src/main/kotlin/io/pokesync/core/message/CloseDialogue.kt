package io.pokesync.core.message

import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * A command to close a dialogue.
 * @author Sino
 */
object CloseDialogue : Message {
    /**
     * Creates a [PacketToMessageDecoder] to encode [CloseDialogue] messages.
     */
    fun decoder(): PacketToMessageDecoder<CloseDialogue> =
        object : PacketToMessageDecoder<CloseDialogue> {
            override fun decode(packet: Packet): CloseDialogue =
                CloseDialogue
        }
}