package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * A command to set the amount of donator points a user has.
 * @author Sino
 */
data class SetDonatorPoints(val points: Int) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [SetDonatorPoints]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<SetDonatorPoints> =
            object : PacketToMessageDecoder<SetDonatorPoints> {
                override fun decode(packet: Packet): SetDonatorPoints {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())
                    val points = buffer.readInt() and 0xFFFFFF

                    return SetDonatorPoints(points)
                }
            }
    }
}