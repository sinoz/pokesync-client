package io.pokesync.core.assets.config

import arrow.effects.IO
import io.netty.buffer.Unpooled
import io.pokesync.core.assets.exception.UnexpectedFileStampException
import io.pokesync.lib.bytes.readString
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Contains a list of indices that contain the locations of each and every
 * map to define the complete world.
 * @author Sino
 */
data class WorldConfig(val width: Int, val length: Int, val regions: List<RegionIndex>) {
    /**
     * A single region entry.
     */
    data class RegionIndex(
        val maps: List<MapIndex>
    )

    /**
     * A single map index entry on the global grid.
     */
    data class MapIndex(
        val mapX: Int,
        val mapZ: Int,
        val renderX: Int,
        val renderY: Int
    )

    companion object {
        /**
         * Loads a binary config file at the specified [Path].
         */
        fun load(path: Path): IO<WorldConfig> = IO {
            val bytes = Files.readAllBytes(path)
            val buffer = Unpooled.wrappedBuffer(bytes)

            val dataFileStamp = buffer.readString(4)
            if (dataFileStamp != ConfigType.WORLD.fileStamp) {
                throw UnexpectedFileStampException(dataFileStamp)
            }

            val headerLength = buffer.readUnsignedByte()
            if (headerLength < 0 || headerLength > buffer.readableBytes()) {
                throw IOException()
            }

            buffer.readUnsignedByte() // export version

            val payloadLength = buffer.readInt()
            val worldCount = buffer.readUnsignedShort()

            if (payloadLength < 0 || payloadLength > buffer.readableBytes()) {
                throw IOException()
            }

            if (worldCount > 1) {
                throw IOException()
            }

            val worldWidth = buffer.readUnsignedShort()
            val worldLength = buffer.readUnsignedShort()

            val regionCount = buffer.readUnsignedByte()
            val regions = mutableListOf<RegionIndex>()
            for (regionId in 0 until regionCount) {
                val mapCount = buffer.readUnsignedShort()
                val maps = mutableListOf<MapIndex>()
                for (mapIdx in 0 until mapCount) {
                    val mapX = buffer.readUnsignedShort()
                    val mapZ = buffer.readUnsignedShort()

                    val renderX = buffer.readUnsignedShort()
                    val renderY = buffer.readUnsignedShort()

                    maps.add(MapIndex(mapX, mapZ, renderX, renderY))
                }

                regions.add(RegionIndex(maps))
            }

            WorldConfig(worldWidth, worldLength, regions)
        }
    }
}