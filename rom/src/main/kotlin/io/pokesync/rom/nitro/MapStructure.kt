package io.pokesync.rom.nitro

/**
 * The structure of a single game map.
 * @author Sino
 */
class MapStructure(
    val tiles: Array<Array<Tile?>>,
    val landscape: List<ObjectEntry>
) {
    /**
     * A flat tile entry on the map.
     */
    data class Tile(
        val collisionFlag: Int,
        val movementType: Int
    )

    /**
     * A 3D object entry on the landscape part of the map.
     */
    data class ObjectEntry(
        val id: Int,
        val xFlag: Int,
        val x: Int,
        val zFlag: Int,
        val z: Int,
        val yFlag: Int,
        val y: Int,
        val width: Int,
        val height: Int,
        val length: Int
    )
}