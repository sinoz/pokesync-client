package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder
import io.pokesync.lib.bytes.readCString

/**
 * A type of command to display a chat message sent by another user.
 * @author Sino
 */
data class DisplayChatMessage(
    val channelId: Int,
    val displayName: DisplayName,
    val text: String
) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [DisplayChatMessage]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<DisplayChatMessage> =
            object : PacketToMessageDecoder<DisplayChatMessage> {
                override fun decode(packet: Packet): DisplayChatMessage {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())

                    val channelId = buffer.readUnsignedByte().toInt()
                    val displayName = buffer.readCString()
                    val text = buffer.readCString()

                    return DisplayChatMessage(channelId, DisplayName(displayName), text)
                }
            }
    }
}