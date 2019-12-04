package io.pokesync.lib.bytes

import com.google.protobuf.ByteString
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlin.math.min

/**
 * Unpacks a single byte with 2-bit values packed into four separate bytes.
 */
fun ByteString.unpackBit2(): ByteString {
    val originalSize = size()
    val unpackedLength = size() * 4

    val unpacked = ByteArray(unpackedLength)
    for (i in 0 until originalSize) {
        unpacked[i * 4] = (byteAt(i).toInt() and 0x3).toByte()
        unpacked[(i * 4) + 1] = ((byteAt(i).toInt() shr 2) and 0x3).toByte()
        unpacked[(i * 4) + 2] = ((byteAt(i).toInt() shr 4) and 0x3).toByte()
        unpacked[(i * 4) + 3] = ((byteAt(i).toInt() shr 6) and 0x3).toByte()
    }

    return ByteString.copyFrom(unpacked)
}

/**
 * Unpacks a single byte with 4-bit values packed into two separate bytes.
 */
fun ByteString.unpackBit4(): ByteString {
    val originalSize = size()
    val unpackedLength = size() * 2

    val unpacked = ByteArray(unpackedLength)
    for (i in 0 until originalSize) {
        unpacked[i * 2] = (byteAt(i).toInt() and 0x0F).toByte()
        unpacked[(i * 2) + 1] = ((byteAt(i).toInt() and 0xF0) shr 4).toByte()
    }

    return ByteString.copyFrom(unpacked)
}

/**
 * Unpacks an array of 16-bit values packed into four separate bytes.
 */
fun unpackBit16(array: IntArray): ByteString { // TODO use ByteString as input instead
    val originalSize = array.size
    val unpackedLength = array.size * 4

    val unpacked = ByteArray(unpackedLength)
    for (i in 0 until originalSize) {
        unpacked[i * 4] = (array[i] and 0xF).toByte()
        unpacked[(i * 4) + 1] = ((array[i] shr 4) and 0xF).toByte()
        unpacked[(i * 4) + 2] = ((array[i] shr 8) and 0xF).toByte()
        unpacked[(i * 4) + 3] = ((array[i] shr 12) and 0xF).toByte()
    }

    return ByteString.copyFrom(unpacked)
}

/**
 * Wraps a byte array extracted from the [ByteString], into the [ByteBuf].
 */
fun ByteString.asWritableByteBuf(): ByteBuf =
    Unpooled.wrappedBuffer(toByteArray())

/**
 * Wraps a read-only [java.nio.ByteBuffer] with Netty's [ByteBuf].
 */
fun ByteString.asReadOnlyByteBuf(): ByteBuf =
    Unpooled.wrappedBuffer(asReadOnlyByteBuffer())

/**
 * An auxiliary function for [ByteString.substring] to
 * drop a specified amount of bytes from the [ByteString].
 */
fun ByteString.dropBytes(amount: Int): ByteString =
    substring(amount)

/**
 * An auxiliary function for [ByteString.substring] to take a
 * [ByteString] slice.
 */
fun ByteString.slice(from: Int, size: Int): ByteString =
    substring(from, min(size(), from + size))