package io.pokesync.lib.bytes

import com.google.protobuf.ByteString
import io.netty.buffer.ByteBuf

/**
 * Reads the remaining amount of bytes in this [ByteBuf] into
 * a [ByteString].
 */
fun ByteBuf.toByteString(size: Int = readableBytes()): ByteString {
    val bytes = ByteArray(size)
    readBytes(bytes)
    return ByteString.copyFrom(bytes)
}

/**
 * Reads the specified amount of bytes into a [StringBuilder]
 * and outputs the sequence of bytes as a [String].
 */
fun ByteBuf.readString(length: Int): String {
    val bldr = StringBuilder()

    for (i in 0 until length) {
        if (!isReadable) {
            break
        }

        bldr.append(readUnsignedByte().toChar())
    }

    return bldr.toString()
}

/**
 * Continuously reads and appends bytes into a [StringBuilder]
 * until a value of 0 has been read from the buffer.
 */
fun ByteBuf.readCString(): String {
    val bldr = StringBuilder()

    while (isReadable) {
        val value = readUnsignedByte().toInt()
        if (value == 0) {
            break
        }

        bldr.append(value.toChar())
    }

    return bldr.toString()
}

/**
 * Continuously writes each character in the given [String]
 * to the buffer and appends a NULL_TERMINATOR value at the end
 * of the sequence.
 */
fun ByteBuf.writeCString(value: String) {
    value.forEach { writeByte(it.toInt()) }
    writeByte(0)
}