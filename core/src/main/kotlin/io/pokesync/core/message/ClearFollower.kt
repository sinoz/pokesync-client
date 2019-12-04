package io.pokesync.core.message

import com.google.protobuf.ByteString
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet

/**
 * A command to clear a follower monster.
 * @author Sino
 */
object ClearFollower : Message {
    /**
     * Creates a [MessageToPacketEncoder] to encode [ClearFollower] messages.
     */
    fun encoder(): MessageToPacketEncoder<ClearFollower> =
        object : MessageToPacketEncoder<ClearFollower> {
            override fun encode(message: ClearFollower): Packet =
                Packet(Packet.Kind.CLEAR_FOLLOWER, ByteString.EMPTY)
        }
}