package io.pokesync.core.message

import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder

/**
 * A command to reset the user's camera to its default position.
 * @author Sino
 */
object ResetOrthographicCamera : Message {
    /**
     * Creates a [PacketToMessageDecoder] to decode [ResetOrthographicCamera]
     * messages.
     */
    fun decoder(): PacketToMessageDecoder<ResetOrthographicCamera> =
        object : PacketToMessageDecoder<ResetOrthographicCamera> {
            override fun decode(packet: Packet): ResetOrthographicCamera =
                ResetOrthographicCamera
        }
}