package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.account.UserGroup
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.game.model.MapPosition
import io.pokesync.core.game.world.component.PID
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder
import io.pokesync.lib.bytes.readCString

/**
 * An event of the user's viewable map having been updated.
 * @author Sino
 */
data class MapRefreshed(val mapX: Int, val mapZ: Int) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [MapRefreshed] messages.
         */
        fun decoder(): PacketToMessageDecoder<MapRefreshed> =
            object : PacketToMessageDecoder<MapRefreshed> {
                override fun decode(packet: Packet): MapRefreshed {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())

                    val mapX = buffer.readUnsignedShort()
                    val mapZ = buffer.readUnsignedShort()

                    return MapRefreshed(mapX, mapZ)
                }
            }
    }
}