package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * A command to update the server time display.
 * @author Sino
 */
data class SetServerTime(val hours: Int, val minutes: Int) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [SetServerTime]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<SetServerTime> =
            object : PacketToMessageDecoder<SetServerTime> {
                override fun decode(packet: Packet): SetServerTime {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())

                    val hours = buffer.readUnsignedByte().toInt()
                    val minutes = buffer.readUnsignedByte().toInt()

                    return SetServerTime(hours, minutes)
                }
            }
    }
}