package io.pokesync.core.message

import com.google.protobuf.ByteString
import io.pokesync.core.net.MessageToPacketEncoder
import io.pokesync.core.net.Packet

/**
 * A command to close a dialogue.
 * @author Sino
 */
object ContinueDialogue : Message {
    /**
     * Creates a [MessageToPacketEncoder] to encode [ContinueDialogue] messages.
     */
    fun encoder(): MessageToPacketEncoder<ContinueDialogue> =
        object : MessageToPacketEncoder<ContinueDialogue> {
            override fun encode(message: ContinueDialogue): Packet =
                Packet(Packet.Kind.CONTINUE_DIALOGUE, ByteString.EMPTY)
        }
}