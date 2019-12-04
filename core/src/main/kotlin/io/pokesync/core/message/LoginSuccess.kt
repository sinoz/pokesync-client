package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.account.UserGroup
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MapPosition
import io.pokesync.core.game.world.component.PID
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder
import io.pokesync.lib.bytes.readCString

/**
 * An event of the user's login attempt being successful.
 * @author Sino
 */
data class LoginSuccess(
    val pid: PID,
    val gender: Gender,
    val displayName: DisplayName,
    val userGroup: UserGroup,
    val position: MapPosition
) : Message {
    companion object {
        /**
         * Creates a [PacketToMessageDecoder] to decode [LoginSuccess]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<LoginSuccess> =
            object : PacketToMessageDecoder<LoginSuccess> {
                override fun decode(packet: Packet): LoginSuccess {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())

                    val pid = PID(buffer.readUnsignedShort())

                    val displayName = DisplayName(buffer.readCString())

                    val gender = Gender.fromId(buffer.readUnsignedByte().toInt())!!
                    val userGroup = UserGroup.fromId(buffer.readUnsignedByte().toInt())

                    val mapX = buffer.readUnsignedShort()
                    val mapZ = buffer.readUnsignedShort()

                    val localX = buffer.readUnsignedShort()
                    val localZ = buffer.readUnsignedShort()

                    val position = MapPosition(mapX, mapZ, localX, localZ)

                    return LoginSuccess(pid, gender, displayName, userGroup, position)
                }
            }
    }
}