package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.model.MovementType
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString

/**
 * A command to move an avatar a step in a [Direction].
 * @author Sino
 */
data class MoveAvatar(val direction: Direction) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [MoveAvatar] messages.
         */
        fun encoder(): MessageToPacketEncoder<MoveAvatar> =
            object : MessageToPacketEncoder<MoveAvatar> {
                override fun encode(message: MoveAvatar): Packet {
                    val buffer = Unpooled.buffer()
                    buffer.writeByte(Direction.toId(message.direction))

                    return Packet(Packet.Kind.MOVE_AVATAR, buffer.toByteString())
                }
            }
    }
}