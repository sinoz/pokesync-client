package io.pokesync.core.assets.config

import arrow.effects.IO
import io.netty.buffer.Unpooled
import io.pokesync.core.assets.exception.UnexpectedFileStampException
import io.pokesync.lib.bytes.readCString
import io.pokesync.lib.bytes.readString
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Contains information of each object.
 * @author Sino
 */
data class ObjectConfig(val entries: List<Entry>) {
    data class Entry(
        val id: Int,
        val name: String
    )

    companion object {
        /**
         * Loads a binary config file at the specified [Path].
         */
        fun load(path: Path): IO<ObjectConfig> = IO {
            val bytes = Files.readAllBytes(path)
            val buffer = Unpooled.wrappedBuffer(bytes)

            val dataFileStamp = buffer.readString(4)
            if (dataFileStamp != ConfigType.OBJECTS.fileStamp) {
                throw UnexpectedFileStampException(dataFileStamp)
            }

            val headerLength = buffer.readUnsignedByte()
            if (headerLength < 0 || headerLength > buffer.readableBytes()) {
                throw IOException()
            }

            buffer.readUnsignedByte() // export version

            val payloadLength = buffer.readInt()
            val itemCount = buffer.readUnsignedShort()

            if (payloadLength < 0 || payloadLength > buffer.readableBytes()) {
                throw IOException()
            }

            val objectEntries = mutableListOf<Entry>()
            for (i in 0 until itemCount) {
                val id = buffer.readUnsignedShort()
                val name = buffer.readCString()

                objectEntries.add(Entry(id, name))
            }

            ObjectConfig(objectEntries)
        }
    }
}