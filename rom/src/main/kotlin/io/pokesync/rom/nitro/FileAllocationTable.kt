package io.pokesync.rom.nitro

import com.google.protobuf.ByteString
import io.pokesync.lib.bytes.asReadOnlyByteBuf

/**
 * A table of indices that point to a file within memory.
 * @author Sino
 */
data class FileAllocationTable(val indices: List<Index>) {
    companion object {
        /**
         * The amount of bytes each entry occupies within the table.
         */
        private const val ENTRY_SIZE = 8

        /**
         * Holds the address and size of a single file in memory.
         */
        data class Index(
            val address: Int,
            val size: Int
        )

        /**
         * Reads a [FileAllocationTable] from the given [ByteString].
         */
        fun read(bytes: ByteString, fileCount: Int = bytes.size() / ENTRY_SIZE): FileAllocationTable {
            val iterator = bytes.asReadOnlyByteBuf()
            val indexList = mutableListOf<Index>()
            for (fileId in 0 until fileCount) {
                val startAddress = iterator.readIntLE()
                val endAddress = iterator.readIntLE()

                indexList.add(Index(startAddress, endAddress - startAddress))
            }

            return FileAllocationTable(indexList)
        }
    }
}