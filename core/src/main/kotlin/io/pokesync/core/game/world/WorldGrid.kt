package io.pokesync.core.game.world

import arrow.effects.IO
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import io.pokesync.core.assets.config.WorldConfig
import java.nio.file.Path
import java.nio.file.Paths

/**
 * A large grid of [TiledMap]s.
 * @author Sino
 */
class WorldGrid(private val maps: Array<Array<TiledMap?>>) {
    /**
     * Looks up a [TiledMap] in this grid.
     */
    fun lookupMap(x: Int, y: Int): TiledMap? =
        if (x < 0 || x >= maps.size) {
            null
        } else if (y < 0 || y >= maps.size) {
            null
        } else {
            maps[x][y]
        }

    fun width(): Int =
        maps.size

    fun length(): Int =
        maps[0].size

    companion object {
        /**
         * The [Path] to all of the maps.
         */
        private val MAPS_DIR: Path = Paths.get("resources/data/tile/tmx/")

        /**
         * An effect that constructs a [WorldGrid] from the given [WorldConfig] by
         * loading all of the enlisted maps into memory.
         */
        fun fromConfig(config: WorldConfig): IO<WorldGrid> = IO {
            val worldWidth = config.width
            val worldLength = config.length

            val mapLoader = TmxMapLoader()
            val maps = Array<Array<TiledMap?>>(worldWidth) { arrayOfNulls(worldLength) }

            for (regionId in config.regions.indices) {
                val region = config.regions[regionId]
                for (mapIndex in region.maps) {
                    val mapX = mapIndex.mapX
                    val mapY = mapIndex.mapZ

                    val originX = mapIndex.renderX
                    val originY = mapIndex.renderY

                    val tmxFilePath = MAPS_DIR.resolve("${regionId}_${mapX}_${mapY}.tmx").toString()
                    val map = mapLoader.load(tmxFilePath)

                    for (layer in map.layers) {
                        val tiledMapLayer = layer as TiledMapTileLayer

                        val globalOriginPixelX = originX * tiledMapLayer.tileWidth
                        val globalOriginPixelY = originY * tiledMapLayer.tileHeight

                        tiledMapLayer.offsetX = globalOriginPixelX
                        tiledMapLayer.offsetY = -globalOriginPixelY

                        tiledMapLayer.invalidateRenderOffset()
                    }

                    map.properties.put("mx", mapX)
                    map.properties.put("my", mapY)

                    map.properties.put("ox", originX)
                    map.properties.put("oy", originY)

                    maps[mapX][mapY] = map
                }
            }

            WorldGrid(maps)
        }
    }
}