package io.pokesync.core.game.model

import com.badlogic.gdx.math.Vector2
import io.pokesync.core.game.world.WorldGrid
import java.lang.IllegalArgumentException

/**
 * An exact tile position on the game map.
 * @author Sino
 */
data class MapPosition(
    val mapX: Int,
    val mapZ: Int,
    val localX: Int,
    val localZ: Int
) {
    companion object {
        /**
         * Translates the given [Vector2] with world coordinates to a [MapPosition]
         * based off the given [WorldGrid].
         */ // TODO optimize from an O(N ^ 2) search to O(1) or O(N)
        fun fromWorldCoordinates(point: Vector2, worldGrid: WorldGrid): MapPosition {
            for (x in 0 until worldGrid.width()) {
                for (z in 0 until worldGrid.length()) {
                    val map = worldGrid.lookupMap(x, z) ?: continue

                    val width = map.properties["width"] as Int
                    val length = map.properties["height"] as Int

                    val originX = map.properties["ox"] as Int
                    val originZ = map.properties["oy"] as Int

                    val localX = point.x - originX
                    val localZ = point.y - originZ
                    if (localX >= 0 && localZ >= 0 && localX < width && localZ < length) {
                        return MapPosition(x, z, localX.toInt(), localZ.toInt())
                    }
                }
            }

            throw IllegalArgumentException()
        }

        /**
         * Translates the given [MapPosition] to world coordinates based off the given
         * [WorldGrid]. The world coordinates are returned in a [Vector2] format.
         */
        fun toWorldCoordinates(position: MapPosition, worldGrid: WorldGrid): Vector2 {
            val map = worldGrid.lookupMap(position.mapX, position.mapZ)!!

            val width = map.properties["width"] as Int
            val length = map.properties["height"] as Int

            val originX = map.properties["ox"] as Int
            val originZ = map.properties["oz"] as Int

            val worldX = originX + position.localX
            val worldZ = originZ + position.localZ

            require(!(worldX < 0 || worldX >= (originX + width) || worldZ < 0 || worldZ >= (originZ + length)))

            return Vector2(worldX.toFloat(), worldZ.toFloat())
        }
    }
}