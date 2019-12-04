package io.pokesync.rom.nitro

import io.pokesync.lib.bytes.asReadOnlyByteBuf
import io.pokesync.lib.bytes.readString
import io.pokesync.lib.bytes.slice
import io.pokesync.rom.DiskFile
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap

/**
 * A nitro image.
 * @author Sino
 */
class Image(val diskFile: DiskFile, val header: ImageHeader, val fileSystem: FileSystem) {
    companion object {
        /**
         * Constructs a Nitro [Image] from the given [DiskFile].
         */
        fun create(diskFile: DiskFile): Image {
            val imageHeader = ImageHeader.readFromDiskFile(diskFile)

            val fileAllocTableBlock = diskFile.bytes.slice(imageHeader.fileAllocTableAddr, imageHeader.fileAllocTableSize)
            val fileAllocTable = FileAllocationTable.read(fileAllocTableBlock)

            val fileSystem = readFileSystem(diskFile, imageHeader.fileNameTableAddr, fileAllocTable)

            return Image(diskFile, imageHeader, fileSystem)
        }

        /**
         * Reads the very root [FileSystem] within the Nitro image [DiskFile].
         */
        private fun readFileSystem(diskFile: DiskFile, fileNameTableAddress: Int, fat: FileAllocationTable): FileSystem {
            val files = Int2ObjectAVLTreeMap<File>()

            val tableBlock = diskFile.bytes.substring(fileNameTableAddress)
            val tableIterator = tableBlock.asReadOnlyByteBuf()

            val directoryCount = tableIterator.getUnsignedShortLE(6)
            for (directoryId in 0 until directoryCount) {
                val address = tableIterator.readIntLE()
                val firstFileId = tableIterator.readUnsignedShortLE()

                tableIterator.readUnsignedShortLE() // id of the first directory which isn't used

                val directoryBlock = diskFile.bytes.substring(fileNameTableAddress + address)
                val directoryIterator = directoryBlock.asReadOnlyByteBuf()

                var nameLength = directoryIterator.readUnsignedByte().toInt()
                var fileId = firstFileId
                while (nameLength != 0) {
                    val isFile = nameLength < 128
                    if (isFile) {
                        val lengthOfFileName = nameLength

                        val fileName = directoryIterator.readString(lengthOfFileName)
                        val fileFatEntry = fat.indices[fileId]

                        val fileBytes = diskFile.bytes.slice(fileFatEntry.address, fileFatEntry.size)
                        val fileHeader = FileHeader.readFromFile(fileBytes)

                        files[fileId] = File(File.Companion.Metadata(fileId, fileName, fileHeader.extension, fileHeader.fileSize), fileBytes)

                        fileId++
                    } else {
                        val lengthOfDirectoryName = nameLength - 128

                        directoryIterator.readString(lengthOfDirectoryName) // name of the directory
                        directoryIterator.readShortLE() // id of the directory
                    }

                    nameLength = directoryIterator.readUnsignedByte().toInt()
                }
            }

            return FileSystem(files)
        }
    }
}