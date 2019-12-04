package io.pokesync.rom.crypto

import com.google.protobuf.ByteString
import io.pokesync.lib.bytes.asReadOnlyByteBuf

/**
 * The multiplier value to multiply the seed with on each iteration during
 * stream deciphering.
 */
private const val MULTIPLIER = 1103515245

/**
 * The value to add on each iteration to the seed during stream deciphering.
 */
private const val ADD = 24691

/**
 * Deciphers a [ByteString] according to the 'Diamond-And Pearl' encryption
 * algorithm, which is a tiny stream cipher based on the Pokemon RNG. The
 * 'Diamond-And Pearl' encryption is a reversed variant of the 'Platinum'
 * encryption algorithm.
 */
fun ByteString.decipherDiamondAndPearl(): IntArray { // TODO output a ByteString instead
    val buffer = asReadOnlyByteBuf()
    val seeds = IntArray(buffer.readableBytes() / 2)
    for (i in seeds.indices) {
        seeds[i] = buffer.readShortLE().toInt()
    }

    var seed = seeds[seeds.size - 1]
    for (i in seeds.indices.reversed()) {
        seeds[i] = seeds[i] xor seed
        seed = (seed * MULTIPLIER) + ADD
    }

    return seeds
}

/**
 * Deciphers a [ByteString] according to the 'Platinum' encryption
 * algorithm, which is a tiny stream cipher based on the Pokemon RNG.
 */
fun ByteString.decipherPlatinum(): IntArray { // TODO output a ByteString instead
    val buffer = asReadOnlyByteBuf()
    val seeds = IntArray(buffer.readableBytes() / 2)
    for (i in seeds.indices) {
        seeds[i] = buffer.readShortLE().toInt()
    }

    var seed = seeds[0]
    for (i in seeds.indices) {
        seeds[i] = seeds[i] xor seed
        seed = (seed * MULTIPLIER) + ADD
    }

    return seeds
}