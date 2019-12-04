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
 * A command to switch two party slots.
 * @author Sino
 */
data class SwitchPartySlots(val slotA: Int, val slotB: Int) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [SwitchPartySlots] messages.
         */
        fun encoder(): MessageToPacketEncoder<SwitchPartySlots> =
            object : MessageToPacketEncoder<SwitchPartySlots> {
                override fun encode(message: SwitchPartySlots): Packet {
                    val buffer = Unpooled.buffer()

                    buffer.writeByte(message.slotA)
                    buffer.writeByte(message.slotB)

                    return Packet(Packet.Kind.SWITCH_PARTY_SLOTS, buffer.toByteString())
                }
            }
    }
}