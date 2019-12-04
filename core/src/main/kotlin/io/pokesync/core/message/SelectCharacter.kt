package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.account.Email
import io.pokesync.core.account.Password
import io.pokesync.core.client.BuildNumber
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString
import io.pokesync.lib.bytes.writeCString

/**
 * A command to select a game character.
 * @author Sino
 */
data class SelectCharacter(val id: Int) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [SelectCharacter] messages.
         */
        fun encoder(): MessageToPacketEncoder<SelectCharacter> =
            object : MessageToPacketEncoder<SelectCharacter> {
                override fun encode(message: SelectCharacter): Packet {
                    val buffer = Unpooled.buffer()
                    buffer.writeByte(message.id)

                    return Packet(Packet.Kind.SELECT_CHARACTER, buffer.toByteString())
                }
            }
    }
}