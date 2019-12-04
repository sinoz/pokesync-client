package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString
import io.pokesync.lib.bytes.writeCString

/**
 * A command to submit a chat command.
 * @author Sino
 */
data class SubmitChatCommand(val trigger: String, val arguments: List<String>) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [SubmitChatCommand] messages.
         */
        fun encoder(): MessageToPacketEncoder<SubmitChatCommand> =
            object : MessageToPacketEncoder<SubmitChatCommand> {
                override fun encode(message: SubmitChatCommand): Packet {
                    val buffer = Unpooled.buffer()

                    buffer.writeCString(message.trigger)

                    message.arguments.forEach { buffer.writeCString(it) }

                    return Packet(Packet.Kind.SUBMIT_CHAT_CMD, buffer.toByteString())
                }
            }
    }
}