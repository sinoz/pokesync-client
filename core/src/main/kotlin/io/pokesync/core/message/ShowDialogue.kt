package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder
import io.pokesync.lib.bytes.readCString

/**
 * A command to show a piece of dialogue.
 * @author Sino
 */
data class ShowDialogue(val text: String) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [ShowDialogue]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<ShowDialogue> =
            object : PacketToMessageDecoder<ShowDialogue> {
                override fun decode(packet: Packet): ShowDialogue {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())
                    val text = buffer.readCString()

                    return ShowDialogue(text)
                }
            }
    }
}