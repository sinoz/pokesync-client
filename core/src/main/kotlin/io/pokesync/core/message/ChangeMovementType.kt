package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.game.model.MovementType
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString

/**
 * A command to change the avatar's movement type.
 * @author Sino
 */
data class ChangeMovementType(val movementType: MovementType) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [ChangeMovementType] messages.
         */
        fun encoder(): MessageToPacketEncoder<ChangeMovementType> =
            object : MessageToPacketEncoder<ChangeMovementType> {
                override fun encode(message: ChangeMovementType): Packet {
                    val buffer = Unpooled.buffer()
                    buffer.writeByte(MovementType.toId(message.movementType))

                    return Packet(Packet.Kind.CHANGE_MOVE_TYPE, buffer.toByteString())
                }
            }
    }
}