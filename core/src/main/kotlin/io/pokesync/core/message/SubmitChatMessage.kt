package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString
import io.pokesync.lib.bytes.writeCString

/**
 * A command to submit a public chat message.
 * @author Sino
 */
data class SubmitChatMessage(val text: String) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [SubmitChatMessage] messages.
         */
        fun encoder(): MessageToPacketEncoder<SubmitChatMessage> =
            object : MessageToPacketEncoder<SubmitChatMessage> {
                override fun encode(message: SubmitChatMessage): Packet {
                    val buffer = Unpooled.buffer()

                    buffer.writeCString(message.text)

                    return Packet(Packet.Kind.SUBMIT_CHAT_MSG, buffer.toByteString())
                }
            }
    }
}