package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.math.Vector2
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.model.Movement
import io.pokesync.core.game.model.MovementType
import io.pokesync.core.game.world.WorldGrid
import io.pokesync.core.game.world.component.*
import io.pokesync.core.game.world.component.BaseSprite.Companion.getCyclingStanceTextureByDirection
import io.pokesync.core.game.world.component.BaseSprite.Companion.getCyclingStepTextureByDirection
import io.pokesync.core.game.world.tile.COLLISION_LAYER
import io.pokesync.core.game.world.tile.TILE_SIZE
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.max
import kotlin.math.min

/**
 * An entity system that provides cycling functionality to entities.
 * @author Sino
 */
// TODO eventually merge TeleportSystem, WalkingSystem and CyclingSystem
class CyclingSystem(val worldGrid: WorldGrid) :
    IteratingSystem(allOf(BaseSprite::class, Transformable::class, HasMotion::class, Bicycle::class).get()) {
    override fun processEntity(entity: Entity, delta: Float) {
        val baseSprite = entity.get<BaseSprite>()!!
        val motion = entity.get<HasMotion>()!!
        val transform = entity.get<Transformable>()!!

        if (transform.getMovementType() != MovementType.CYCLE) {
            return
        }

        val directionToFace = transform.pollDirectionToFace()
        if (directionToFace != null) {
            baseSprite.setRegion(getCyclingStanceTextureByDirection(baseSprite, directionToFace))
        }

        if (transform.getMovement() == null) {
            val direction = transform.pollStep()
            if (direction != null) {
                val destination = when (direction) {
                    Direction.NORTH -> Vector2(transform.position.x, transform.position.y + 1F)
                    Direction.EAST -> Vector2(transform.position.x + 1F, transform.position.y)
                    Direction.WEST -> Vector2(transform.position.x - 1F, transform.position.y)
                    Direction.SOUTH -> Vector2(transform.position.x, transform.position.y - 1F)
                }

                transform.setMovement(Movement(transform.position, destination, direction))
            }

            if (transform.getMovement() == null) {
                return
            }
        }

        val movement = transform.getMovement()!!

        baseSprite.setRegion(getCyclingStepTextureByDirection(baseSprite, movement.direction!!, transform.onLeftStep()))

        val currentX = transform.position.x
        val currentZ = transform.position.y

        val sourceX = movement.source.x
        val sourceZ = movement.source.y

        val destinationX = movement.destination.x
        val destinationZ = movement.destination.y

        val mapX = transform.mapX
        val mapZ = transform.mapZ

        val map = worldGrid.lookupMap(mapX, mapZ)!!
        val layer = map.layers[COLLISION_LAYER]
        if (layer != null) {
            val collisionLayer = layer as TiledMapTileLayer

            val offsetX = map.properties["ox"] as Int
            val offsetY = map.properties["oy"] as Int

            // TODO also take walking into adjacent maps into account
            val localX = (destinationX - offsetX).toInt()
            val localZ = (destinationZ - offsetY).toInt()

            val cell = collisionLayer.getCell(localX, localZ)
            if (cell != null) {
                // QUICKFIX: the properties from a collection of StaticTiledMapTile aren't copied over to the
                // AnimatedTiledMapTile which turns the AnimatedTileMapTile into a tile without any properties!
                var tileProps = cell.tile.properties
                if (cell.tile is AnimatedTiledMapTile) {
                    tileProps = (cell.tile as AnimatedTiledMapTile).frameTiles[0].properties
                }

                val xJumpFrom = (tileProps["x_jump_from"] ?: 0) as Int
                val yJumpFrom = (tileProps["y_jump_from"] ?: 0) as Int
                if (xJumpFrom != 0 || yJumpFrom != 0) {
                    check(!(xJumpFrom != 0 && yJumpFrom != 0))

                    val jumpComponent = entity.get<CanJump>()
                    if (jumpComponent != null) {
                        val standingOnLedgersGoodSide =
                            (destinationX + xJumpFrom) == sourceX && (destinationZ + yJumpFrom) == sourceZ
                        if (!standingOnLedgersGoodSide) {
                            baseSprite.setRegion(getCyclingStanceTextureByDirection(baseSprite, movement.direction))

                            transform.stopMoving()
                            transform.alternateStep()

                            return
                        }

                        baseSprite.setRegion(getCyclingStanceTextureByDirection(baseSprite, movement.direction))

                        val jumpDestinationX = destinationX.toInt() + (-xJumpFrom)
                        val jumpDestinationY = destinationZ.toInt() + (-yJumpFrom)

                        motion.setJumpingVelocity()

                        transform.stopMoving()
                        transform.jumpTo(jumpDestinationX, jumpDestinationY)

                        return
                    }
                }

                val isSurfable = (tileProps["surfable"] ?: false) as Boolean
                if (isSurfable) {
                    baseSprite.setRegion(getCyclingStanceTextureByDirection(baseSprite, movement.direction))

                    transform.stopMoving()
                    transform.alternateStep()

                    return
                }

                val isDoor = (tileProps["door"] ?: false) as Boolean
                if (isDoor) {
                    baseSprite.setRegion(getCyclingStanceTextureByDirection(baseSprite, movement.direction))

                    transform.stopMoving()
                    transform.alternateStep()

                    val doorOpener = entity.get<CanOpenDoors>()
                    if (doorOpener != null) {
                        doorOpener.tile = Vector2(destinationX, destinationZ)
                    }

                    return
                }

                val isBlocked = (tileProps["blocked"] ?: false) as Boolean
                if (isBlocked) {
                    baseSprite.setRegion(getCyclingStanceTextureByDirection(baseSprite, movement.direction))

                    transform.stopMoving()
                    transform.alternateStep()

                    transform.setCollided(true)

                    return
                }
            }
        }

        var newX = currentX
        var newZ = currentZ

        when (movement.direction) {
            Direction.WEST -> {
                newX = max(destinationX, currentX - (motion.getVelocity() * delta))
            }

            Direction.SOUTH -> {
                newZ = max(destinationZ, currentZ - (motion.getVelocity() * delta))
            }

            Direction.NORTH -> {
                newZ = min(destinationZ, currentZ + (motion.getVelocity() * delta))
            }

            Direction.EAST -> {
                newX = min(destinationX, currentX + (motion.getVelocity() * delta))
            }
        }

        baseSprite.setPosition(newX * TILE_SIZE, newZ * TILE_SIZE)
        transform.position.set(newX, newZ)

        if (newX == destinationX && newZ == destinationZ) {
            baseSprite.setRegion(getCyclingStanceTextureByDirection(baseSprite, movement.direction))

            transform.stopMoving()
            transform.alternateStep()
        }
    }
}