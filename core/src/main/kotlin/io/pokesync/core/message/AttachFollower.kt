package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.model.MovementType
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString

/**
 * A command to have a player entity be followed around by one of the
 * monsters in his/her party on the overworld.
 * @author Sino
 */
data class AttachFollower(val partySlot: Int) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [AttachFollower] messages.
         */
        fun encoder(): MessageToPacketEncoder<AttachFollower> =
            object : MessageToPacketEncoder<AttachFollower> {
                override fun encode(message: AttachFollower): Packet {
                    val buffer = Unpooled.buffer()
                    buffer.writeByte(message.partySlot)

                    return Packet(Packet.Kind.ATTACH_FOLLOWER, buffer.toByteString())
                }
            }
    }
}