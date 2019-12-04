package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * A command to move the user's camera to a specific position.
 * @author Sino
 */
data class MoveOrthographicCamera(
    val x: Float,
    val y: Float
) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [MoveOrthographicCamera]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<MoveOrthographicCamera> =
            object : PacketToMessageDecoder<MoveOrthographicCamera> {
                override fun decode(packet: Packet): MoveOrthographicCamera {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())

                    val x = buffer.readFloat()
                    val y = buffer.readFloat()

                    return MoveOrthographicCamera(x, y)
                }
            }
    }
}