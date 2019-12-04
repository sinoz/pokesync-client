package io.pokesync.rom.nitro

import com.google.protobuf.ByteString
import io.pokesync.lib.bytes.*
import io.pokesync.rom.DiskFile
import io.pokesync.rom.GameCode

/**
 * The root header block of a NDS memory image.
 * @author Sino
 */
data class ImageHeader(
    val gameTitle: String,
    val gameCode: GameCode?,
    val makerCode: String,
    val unitCode: Int,
    val encryptionSeed: Int,
    val deviceCapacity: Int,

    val ndsRegion: Int,
    val romVersion: Int,

    val arm9RomAddress: Int,
    val arm9EntryAddress: Int,
    val arm9RamAddress: Int,
    val arm9Size: Int,

    val arm7RomAddress: Int,
    val arm7EntryAddress: Int,
    val arm7RamAddress: Int,
    val arm7Size: Int,

    val fileNameTableAddr: Int,
    val fileNameTableSize: Int,
    val fileAllocTableAddr: Int,
    val fileAllocTableSize: Int
) {
    companion object {
        /**
         * The size of the header, in bytes.
         */
        private const val SIZE = 256

        /**
         * Attempts to read a [ImageHeader] straight from the given [DiskFile].
         */
        fun readFromDiskFile(file: DiskFile): ImageHeader =
            read(file.bytes.substring(0, SIZE))

        /**
         * Attempts to read a [ImageHeader] from the given [ByteString].
         */
        fun read(bytes: ByteString): ImageHeader {
            val iterator = bytes.asReadOnlyByteBuf()

            val gameTitle = iterator.readString(12)
            val gameCode = GameCode.fromGameCode(iterator.readString(4))
            val makerCode = iterator.readString(2)

            val unitCode = iterator.readUnsignedByte().toInt()
            val encryptionSeed = iterator.readUnsignedByte().toInt()
            val deviceCapacity = iterator.readUnsignedByte().toInt()

            iterator.skipBytes(7)
            iterator.skipBytes(1)

            val ndsRegion = iterator.readUnsignedByte().toInt()
            val romVersion = iterator.readUnsignedByte().toInt()

            iterator.skipBytes(1)

            val arm9RomAddress = iterator.readIntLE()
            val arm9EntryAddress = iterator.readIntLE()
            val arm9RamAddress = iterator.readIntLE()
            val arm9Size = iterator.readIntLE()

            val arm7RomAddress = iterator.readIntLE()
            val arm7EntryAddress = iterator.readIntLE()
            val arm7RamAddress = iterator.readIntLE()
            val arm7Size = iterator.readIntLE()

            val fileNameTableAddress = iterator.readIntLE()
            val fileNameTableSize = iterator.readIntLE()

            val fileAllocTableAddress = iterator.readIntLE()
            val fileAllocTableSize = iterator.readIntLE()

            return ImageHeader(
                gameTitle, gameCode, makerCode, unitCode, encryptionSeed, deviceCapacity, ndsRegion, romVersion,
                arm9RomAddress, arm9EntryAddress, arm9RamAddress, arm9Size,
                arm7RomAddress, arm7EntryAddress, arm7RamAddress, arm7Size,
                fileNameTableAddress, fileNameTableSize, fileAllocTableAddress, fileAllocTableSize
            )
        }
    }
}