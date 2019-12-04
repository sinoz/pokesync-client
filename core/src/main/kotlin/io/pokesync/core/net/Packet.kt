package io.pokesync.core.net

import com.google.protobuf.ByteString
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.ByteWriteChannel
import kotlinx.coroutines.io.readFully
import kotlinx.coroutines.io.writeFully
import java.nio.ByteBuffer

/**
 * A structured unit of data that can be transmitted across the wire.
 * @author Sino
 */
data class Packet(val kind: Kind, val bytes: ByteString) {
    /**
     * The kind of the packet.
     */
    enum class Kind(val id: Int) {
        /**
         * Client -> Server
         */
        REQUEST_LOGIN(id = 0),
        CREATE_CHARACTER(id = 1),
        SELECT_CHARACTER(id = 2),
        ATTACH_FOLLOWER(id = 3),
        CLEAR_FOLLOWER(id = 4),
        CHANGE_MOVE_TYPE(id = 5),
        CONTINUE_DIALOGUE(id = 6),
        FACE_DIRECTION(id = 7),
        INTERACT_WITH_ENTITY(id = 8),
        MOVE_AVATAR(id = 9),
        SELECT_PLAYER_OPT(id = 10),
        SWITCH_PARTY_SLOTS(id = 11),
        SUBMIT_CHAT_MSG(id = 12),
        SUBMIT_CHAT_CMD(id = 13),
        CLICK_TELEPORT(id = 14),
        SELECT_CHAT_CHAN(id = 15),

        /**
         * Server -> Client
         */
        LOGIN_SUCCESS(id = 255),
        ACCOUNT_DISABLED(id = 254),
        ALREADY_LOGGED_IN(id = 253),
        INVALID_CREDENTIALS(id = 252),
        UNABLE_TO_FETCH_PROFILE(id = 251),
        AUTH_TIMED_OUT(id = 250),
        WORLD_FULL(id = 249),
        DISPLAY_CHAT_MSG(id = 248),
        CLOSE_DIALOGUE(id = 247),
        ENTITY_UPDATE(id = 246),
        MOVE_ORTHO_CAMERA(id = 245),
        RESET_ORTHO_CAMERA(id = 244),
        SET_DONATOR_PTS(id = 243),
        SET_PARTY_SLOT(id = 242),
        SET_POKEDOLLAR(id = 241),
        SET_SERVER_TIME(id = 240),
        SWITCH_CHAT_CHAN(id = 239),
        MAP_REFRESH(id = 238);

        companion object {
            private val mappings = createMapping()

            /**
             * Creates a mapping between packet id's and [Packet.Kind]s.
             */
            private fun createMapping(): Int2ObjectMap<Kind> {
                val kinds = Int2ObjectArrayMap<Kind>()

                for (kind in values()) {
                    check(kinds[kind.id] == null) { "Require unique entries" }

                    kinds[kind.id] = kind
                }

                return kinds
            }

            /**
             * Looks up the [Kind] of the [Packet].
             */
            fun getKindById(id: Int): Kind? =
                mappings[id]
        }
    }

    /**
     * Returns the amount of bytes the header of this [Packet] occupies.
     */
    fun headerLength(): Int =
        1 + computeRawVarInt32Size(payloadLength())

    /**
     * Returns the amount of bytes the payload of this [Packet] occupies.
     */
    fun payloadLength(): Int =
        bytes.size()

    /**
     * Returns the total amount of bytes this [Packet] occupies with
     * a length prefixed.
     */
    fun totalLength(): Int =
        headerLength() + payloadLength()

    companion object {
        /**
         * Forks a [Packet] from the given [ByteString]. May return null
         * if there aren't enough bytes in the [ByteString].
         */
        suspend fun fork(bytes: ByteReadChannel): Packet {
            val kind = Kind.getKindById(bytes.readByte().toInt() and 0xFF)!!

            val length = bytes.readRawVarInt32()
            if (length < 0) {
                throw CorruptedFrameException("negative length $length")
            }

            val payload = ByteArray(length)
            bytes.readFully(payload)

            return Packet(kind, ByteString.copyFrom(payload))
        }

        /**
         * Joins the given [Packet] into a [ByteString].
         */
        suspend fun join(packet: Packet, writer: ByteWriteChannel) {
            val messageBytes = packet.bytes
            val payloadLength = messageBytes.size()

            writer.writeByte(packet.kind.id.toByte())
            writer.writeRawVarInt32(payloadLength)
            writer.writeFully(messageBytes.toByteArray())
        }

        /**
         * Reads an integer whose size may vary from 8-bits up to 32 bits from the
         * given [ByteBuffer].
         */
        private suspend fun ByteReadChannel.readRawVarInt32(): Int {
            var tmp = readByte()
            if (tmp >= 0) {
                return tmp.toInt()
            } else {
                var result = tmp.toInt() and 127

                tmp = readByte()
                if (tmp >= 0) {
                    result = result or (tmp.toInt() shl 7)
                } else {
                    result = result or ((tmp.toInt() and 127) shl 7)
                    tmp = readByte()
                    if (tmp >= 0) {
                        result = result or (tmp.toInt() shl 14)
                    } else {
                        result = result or ((tmp.toInt() and 127) shl 14)
                        tmp = readByte()
                        if (tmp >= 0) {
                            result = result or (tmp.toInt() shl 21)
                        } else {
                            result = result or ((tmp.toInt() and 127) shl 21)
                            tmp = readByte()
                            result = result or (tmp.toInt() shl 28)
                            if (tmp < 0) {
                                throw CorruptedFrameException("malformed varint")
                            }
                        }
                    }
                }

                return result
            }
        }

        /**
         * Writes the given [value] into the [ByteBuffer]. The size of the value may vary
         * from 8-bits all the way up to 32-bits, depending on its value.
         */
        private suspend fun ByteWriteChannel.writeRawVarInt32(value: Int) {
            var valueToWrite = value
            while (true) {
                if ((valueToWrite and 0x7F.inv()) == 0) {
                    writeByte(valueToWrite.toByte())
                    return
                } else {
                    writeByte(((valueToWrite and 0x7F) or 0x80).toByte())
                    valueToWrite = valueToWrite ushr 7
                }
            }
        }

        /**
         * Computes the amount of bytes that the given [value] will occupy
         * in a stream.
         */
        private fun computeRawVarInt32Size(value: Int): Int {
            if ((value and (0xffffffff shl 7).toInt()) == 0) {
                return 1
            }

            if ((value and (0xffffffff shl 14).toInt()) == 0) {
                return 2
            }

            if ((value and (0xffffffff shl 21).toInt()) == 0) {
                return 3
            }

            if ((value and (0xffffffff shl 28).toInt()) == 0) {
                return 4
            }

            return 5
        }
    }
}