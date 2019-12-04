package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.core.game.world.component.Kind
import io.pokesync.core.game.world.component.PID
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString

/**
 * A command to interact with an npc or monster.
 * @author Sino
 */
data class InteractWithEntity(val pid: PID) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [InteractWithEntity] messages.
         */
        fun encoder(): MessageToPacketEncoder<InteractWithEntity> =
            object : MessageToPacketEncoder<InteractWithEntity> {
                override fun encode(message: InteractWithEntity): Packet {
                    val buffer = Unpooled.buffer()

                    buffer.writeShort(message.pid.value)

                    return Packet(Packet.Kind.INTERACT_WITH_ENTITY, buffer.toByteString())
                }
            }
    }
}