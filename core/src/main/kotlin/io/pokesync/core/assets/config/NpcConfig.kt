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
 * Contains information about each npc.
 * @author Sino
 */
data class NpcConfig(val entries: List<Entry>) {
    /**
     * A single npc entry.
     */
    data class Entry(
        val id: Int,
        val name: String,
        val overworldFileId: Int
    )

    companion object {
        /**
         * Loads a binary config file at the specified [Path].
         */
        fun load(path: Path): IO<NpcConfig> = IO {
            val bytes = Files.readAllBytes(path)
            val buffer = Unpooled.wrappedBuffer(bytes)

            val dataFileStamp = buffer.readString(4)
            if (dataFileStamp != ConfigType.NPCS.fileStamp) {
                throw UnexpectedFileStampException(dataFileStamp)
            }

            val headerLength = buffer.readUnsignedByte()
            if (headerLength < 0 || headerLength > buffer.readableBytes()) {
                throw IOException()
            }

            buffer.readUnsignedByte() // export version

            val payloadLength = buffer.readInt()
            val npcCount = buffer.readUnsignedShort()

            if (payloadLength < 0 || payloadLength > buffer.readableBytes()) {
                throw IOException()
            }

            val entries = mutableListOf<NpcConfig.Entry>()
            for (i in 0 until npcCount) {
                val id = buffer.readUnsignedShort()
                val name = buffer.readCString()
                val overworldFileId = buffer.readUnsignedShort()

                entries.add(NpcConfig.Entry(id, name, overworldFileId))
            }

            NpcConfig(entries)
        }
    }
}