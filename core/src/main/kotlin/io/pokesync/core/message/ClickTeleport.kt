package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.game.model.MapPosition
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString

/**
 * A command to click teleport to a position.
 * @author Sino
 */
data class ClickTeleport(val position: MapPosition) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [ClickTeleport] messages.
         */
        fun encoder(): MessageToPacketEncoder<ClickTeleport> =
            object : MessageToPacketEncoder<ClickTeleport> {
                override fun encode(message: ClickTeleport): Packet {
                    val buffer = Unpooled.buffer()

                    buffer.writeShort(message.position.mapX)
                    buffer.writeShort(message.position.mapZ)

                    buffer.writeShort(message.position.localX)
                    buffer.writeShort(message.position.localZ)

                    return Packet(Packet.Kind.CLICK_TELEPORT, buffer.toByteString())
                }
            }
    }
}