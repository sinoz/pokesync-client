package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.game.model.Direction
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString

/**
 * A command to change the avatar's direction it is facing.
 * @author Sino
 */
data class FaceDirection(val direction: Direction) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [FaceDirection] messages.
         */
        fun encoder(): MessageToPacketEncoder<FaceDirection> =
            object : MessageToPacketEncoder<FaceDirection> {
                override fun encode(message: FaceDirection): Packet {
                    val buffer = Unpooled.buffer()
                    buffer.writeByte(Direction.toId(message.direction))

                    return Packet(Packet.Kind.FACE_DIRECTION, buffer.toByteString())
                }
            }
    }
}