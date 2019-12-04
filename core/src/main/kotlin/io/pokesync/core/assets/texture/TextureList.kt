package io.pokesync.core.assets.texture

import arrow.effects.IO
import com.badlogic.gdx.graphics.Pixmap
import io.netty.buffer.Unpooled
import io.pokesync.core.assets.exception.UnexpectedFileStampException
import io.pokesync.lib.bytes.readString
import io.pokesync.lib.bytes.toByteString
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * A set of textures stored in the form of [Pixmap]s.
 * @author Sino
 */
class TextureList private constructor(val list: List<Pixmap>) {
    companion object {
        /**
         * Loads a texture file at the specified [Path].
         */
        fun load(path: Path): IO<TextureList> = IO {
            val bytes = Files.readAllBytes(path)
            val buffer = Unpooled.wrappedBuffer(bytes)

            val dataFileStamp = buffer.readString(4)
            if (dataFileStamp != TextureType.PSTX.fileStamp) {
                throw UnexpectedFileStampException(dataFileStamp)
            }

            val headerLength = buffer.readUnsignedByte()
            if (headerLength < 0 || headerLength > buffer.readableBytes()) {
                throw IOException()
            }

            buffer.readUnsignedByte() // export version

            val payloadLength = buffer.readInt()
            val textureCount = buffer.readUnsignedShort()

            if (payloadLength < 0 || payloadLength > buffer.readableBytes()) {
                throw IOException()
            }

            val pixmaps = mutableListOf<Pixmap>()
            for (i in 0 until textureCount) {
                val imageBytes = buffer.readBytes(payloadLength).toByteString().toByteArray()
                val pixmap = Pixmap(imageBytes, 0, imageBytes.size)

                pixmaps.add(pixmap)
            }

            TextureList(pixmaps)
        }
    }
}