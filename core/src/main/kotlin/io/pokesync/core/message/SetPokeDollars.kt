package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * A command to set the amount of pokedollars a user has.
 * @author Sino
 */
data class SetPokeDollars(val dollars: Int) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [SetPokeDollars]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<SetPokeDollars> =
            object : PacketToMessageDecoder<SetPokeDollars> {
                override fun decode(packet: Packet): SetPokeDollars {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())
                    val amount = buffer.readInt() and 0xFFFFFF

                    return SetPokeDollars(amount)
                }
            }
    }
}