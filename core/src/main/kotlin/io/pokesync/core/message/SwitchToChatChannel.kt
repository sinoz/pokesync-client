package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * A command to switch to the specified channel.
 * @author Sino
 */
data class SwitchToChatChannel(val channelId: Int) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [SwitchToChatChannel]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<SwitchToChatChannel> =
            object : PacketToMessageDecoder<SwitchToChatChannel> {
                override fun decode(packet: Packet): SwitchToChatChannel {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())
                    val channelId = buffer.readUnsignedByte().toInt()

                    return SwitchToChatChannel(channelId)
                }
            }
    }
}