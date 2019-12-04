package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.account.Email
import io.pokesync.core.account.Password
import io.pokesync.core.client.BuildNumber
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString
import io.pokesync.lib.bytes.writeCString

/**
 * A command to create a new game character.
 * @author Sino
 */
data class CreateCharacter(val displayName: DisplayName) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [CreateCharacter] messages.
         */
        fun encoder(): MessageToPacketEncoder<CreateCharacter> =
            object : MessageToPacketEncoder<CreateCharacter> {
                override fun encode(message: CreateCharacter): Packet {
                    val buffer = Unpooled.buffer()
                    buffer.writeCString(message.displayName.str)

                    return Packet(Packet.Kind.CREATE_CHARACTER, buffer.toByteString())
                }
            }
    }
}