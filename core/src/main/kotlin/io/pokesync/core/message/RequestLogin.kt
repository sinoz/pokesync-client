package io.pokesync.core.message

import io.netty.buffer.Unpooled
import io.pokesync.core.account.Email
import io.pokesync.core.account.Password
import io.pokesync.core.client.BuildNumber
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet
import io.pokesync.lib.bytes.toByteString
import io.pokesync.lib.bytes.writeCString

/**
 * A command to request to login into the game.
 * @author Sino
 */
data class RequestLogin(
    val buildNumber: BuildNumber,
    val email: Email,
    val password: Password
) : Message {
    companion object {
        /**
         * Creates a [MessageToPacketEncoder] to encode [RequestLogin] messages.
         */
        fun encoder(): MessageToPacketEncoder<RequestLogin> =
            object : MessageToPacketEncoder<RequestLogin> {
                override fun encode(message: RequestLogin): Packet {
                    val buffer = Unpooled.buffer()

                    buffer.writeByte(message.buildNumber.major)
                    buffer.writeByte(message.buildNumber.minor)
                    buffer.writeByte(message.buildNumber.patch)

                    buffer.writeCString(message.email.value)
                    buffer.writeCString(message.password.value)

                    return Packet(Packet.Kind.REQUEST_LOGIN, buffer.toByteString())
                }
            }
    }
}