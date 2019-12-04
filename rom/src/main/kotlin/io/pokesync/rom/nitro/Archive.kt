package io.pokesync.rom.nitro

import com.google.protobuf.ByteString
import io.netty.buffer.ByteBuf
import io.pokesync.lib.bytes.*
import io.pokesync.rom.UnexpectedFileExtensionException
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap

/**
 * An archive of files.
 * @author Sino
 */
class Archive(val header: FileHeader, val fileSystem: FileSystem) {
    companion object {
        /**
         * An exception to throw when there are multiple directories nested within the archive.
         */
        object NestedArchiveException : Exception()

        /**
         * The amount of bytes a header of an archive yields.
         */
        private const val HEADER_SIZE = 16

        /**
         * Attempts to read an [Archive] from the given [File].
         */
        fun read(file: File): Archive {
            val iterator = file.bytes.dropBytes(HEADER_SIZE).asReadOnlyByteBuf()

            val fileHeader = FileHeader.readFromFile(file.bytes)
            if (fileHeader.extension != FileExtension.ARCHIVE.nitroId) {
                throw UnexpectedFileExtensionException(fileHeader.extension)
            }

            val fileAllocTable = readFileAllocationTable(iterator)
            val fileSystem = readFileSystem(iterator, fileAllocTable)

            return Archive(fileHeader, fileSystem)
        }

        /**
         * Attempts to read a [FileAllocationTable] from the given [ByteString].
         */
        private fun readFileAllocationTable(archiveIterator: ByteBuf): FileAllocationTable {
            val extension = archiveIterator.readString(4)
            if (extension != FileExtension.FAT.nitroId) {
                throw UnexpectedFileExtensionException(extension)
            }

            val sectionSize = archiveIterator.readIntLE()
            val fileCount = archiveIterator.readIntLE()

            val fileAllocTableSize = sectionSize - 12
            val fileAllocTableBlock = archiveIterator.toByteString(fileAllocTableSize)

            return FileAllocationTable.read(fileAllocTableBlock, fileCount)
        }

        /**
         * Reads the [FileSystem] of this archive.
         */
        private fun readFileSystem(archiveIterator: ByteBuf, fat: FileAllocationTable): FileSystem {
            val extension = archiveIterator.readString(4)
            if (extension != FileExtension.FILE_SYSTEM.nitroId) {
                throw UnexpectedFileExtensionException(extension)
            }

            val sectionSize = archiveIterator.readIntLE()

            val fileSystemBytes = archiveIterator.toByteString()
            val fileSystemIterator = fileSystemBytes.asReadOnlyByteBuf()

            val files = Int2ObjectAVLTreeMap<File>()
            var fileId = fileSystemIterator.getUnsignedShortLE(4)

            val directoryCount = fileSystemIterator.getUnsignedShortLE(6)
            if (directoryCount > 1) {
                throw NestedArchiveException
            }

            val fileCount = fat.indices.size
            for (fileIndex in 0 until fileCount) {
                val fileFatEntry = fat.indices[fileId]

                val fileBytes = fileSystemBytes.slice(fileFatEntry.address + sectionSize, fileFatEntry.size)
                val fileIterator = fileBytes.asReadOnlyByteBuf()

                val fileExtension = fileIterator.readString(4)
                val fileName = "$fileIndex.$fileExtension"

                files[fileId] = File(File.Companion.Metadata(fileId, fileName, fileExtension, fileBytes.size()), fileBytes)
                fileId++
            }

            return FileSystem(files)
        }
    }
}