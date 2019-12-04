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
 * A command to select an option on another player's context menu.
 * @author Sino
 */
data class SelectPlayerOption(val slot: Int) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [SelectPlayerOption] messages.
         */
        fun encoder(): MessageToPacketEncoder<SelectPlayerOption> =
            object : MessageToPacketEncoder<SelectPlayerOption> {
                override fun encode(message: SelectPlayerOption): Packet {
                    val buffer = Unpooled.buffer()
                    buffer.writeByte(message.slot)

                    return Packet(Packet.Kind.SELECT_PLAYER_OPT, buffer.toByteString())
                }
            }
    }
}