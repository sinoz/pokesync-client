package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString

/**
 * A command to select a specified channel.
 * @author Sino
 */
data class SelectChatChannel(val channelId: Int) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to decode [SelectChatChannel]
         * messages.
         */
        fun encoder(): MessageToPacketEncoder<SelectChatChannel> =
            object : MessageToPacketEncoder<SelectChatChannel> {
                override fun encode(message: SelectChatChannel): Packet {
                    val buffer = Unpooled.buffer()

                    buffer.writeByte(message.channelId)

                    return Packet(Packet.Kind.SELECT_CHAT_CHAN, buffer.toByteString())
                }
            }
    }
}