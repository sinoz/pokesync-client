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
 * Contains information of each animation.
 * @author Sino
 */
data class AnimSeqConfig(val entries: List<Entry>) {
    data class Entry(
        val id: Int,
        val name: String,
        val source: Int,
        val frames: List<Frame>
    )

    data class Frame(
        val regionId: Int,
        val duration: Long
    )

    companion object {
        /**
         * Loads a binary config file at the specified [Path].
         */
        fun load(path: Path): IO<AnimSeqConfig> = IO {
            val bytes = Files.readAllBytes(path)
            val buffer = Unpooled.wrappedBuffer(bytes)

            val dataFileStamp = buffer.readString(4)
            if (dataFileStamp != ConfigType.ANIMATIONS.fileStamp) {
                throw UnexpectedFileStampException(dataFileStamp)
            }

            val headerLength = buffer.readUnsignedByte()
            if (headerLength < 0 || headerLength > buffer.readableBytes()) {
                throw IOException()
            }

            buffer.readUnsignedByte() // export version

            val payloadLength = buffer.readInt()
            val animationCount = buffer.readUnsignedShort()

            if (payloadLength < 0 || payloadLength > buffer.readableBytes()) {
                throw IOException()
            }

            val animationEntries = mutableListOf<Entry>()
            for (i in 0 until animationCount) {
                val id = buffer.readUnsignedShort()
                val name = buffer.readCString()
                val source = buffer.readUnsignedByte().toInt()

                val frames = mutableListOf<Frame>()
                val frameCount = buffer.readUnsignedShort()
                for (frameId in 0 until frameCount) {
                    val regionId = buffer.readUnsignedShort()
                    val duration = buffer.readLong()

                    frames.add(Frame(regionId, duration))
                }

                animationEntries.add(Entry(id, name, source, frames))
            }

            AnimSeqConfig(animationEntries)
        }
    }
}