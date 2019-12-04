package io.pokesync.rom.nitro

import com.google.protobuf.ByteString
import io.pokesync.lib.bytes.asReadOnlyByteBuf
import io.pokesync.lib.bytes.readString

/**
 * The generic header of a file entry within the file system.
 * @author Sino
 */
data class FileHeader(val extension: String, val headerSize: Int, val fileSize: Int, val sectionCount: Int) {
    companion object {
        /**
         * Attempts to read a [FileHeader] from the given [File].
         */
        fun readFromFile(bytes: ByteString): FileHeader {
            val iterator = bytes.asReadOnlyByteBuf()

            val extension = iterator.readString(4)

            iterator.skipBytes(2)
            iterator.skipBytes(2)

            val fileSize = iterator.readIntLE()
            val headerSize = iterator.readUnsignedShortLE()

            val sectionCount = iterator.readUnsignedShortLE()

            return FileHeader(extension, headerSize, fileSize, sectionCount)
        }
    }
}