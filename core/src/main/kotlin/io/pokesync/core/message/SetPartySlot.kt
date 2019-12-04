package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.core.game.model.StatusCondition
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * A command to set a party slot.
 * @author Sino
 */
data class SetPartySlot(
    val slot: Int,
    val monsterId: ModelId,
    val gender: Gender?,
    val coloration: MonsterColoration,
    val statusCondition: StatusCondition?
) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [SetPartySlot]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<SetPartySlot> =
            object : PacketToMessageDecoder<SetPartySlot> {
                override fun decode(packet: Packet): SetPartySlot {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())

                    val slot = buffer.readUnsignedByte().toInt()
                    val monsterId = ModelId(buffer.readUnsignedShort())
                    val gender = Gender.fromId(buffer.readUnsignedByte().toInt())
                    val coloration = MonsterColoration.fromId(buffer.readUnsignedByte().toInt())
                    val statusCondition = StatusCondition.fromId(buffer.readUnsignedByte().toInt())

                    return SetPartySlot(slot, monsterId, gender, coloration, statusCondition)
                }
            }
    }
}