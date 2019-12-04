package io.pokesync.rom.nitro

import com.google.protobuf.ByteString

/**
 * A file within the Nitro file system.
 * @author Sino
 */
data class File(val metadata: Metadata, val bytes: ByteString) {
    companion object {
        /**
         * Contains metadata about a single file within the Nitro file system.
         */
        data class Metadata(
            val id: Int,
            val name: String,
            val extension: String,
            val sizeInBytes: Int
        )
    }
}