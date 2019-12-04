package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.pokesync.core.game.model.MovementType
import io.pokesync.core.game.world.component.BaseSprite
import io.pokesync.core.game.world.component.Transformable
import io.pokesync.core.game.world.tile.TILE_SIZE
import ktx.ashley.allOf
import ktx.ashley.get

/**
 * An entity system that can teleport entities.
 * @author Sino
 */
// TODO eventually merge TeleportSystem, WalkingSystem and CyclingSystem
class TeleportSystem : IteratingSystem(FAMILY) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val baseSprite = entity.get<BaseSprite>()!!
        val transform = entity.get<Transformable>()!!

        if (transform.getMovementType() != MovementType.TELEPORT) {
            return
        }

        val teleportation = transform.getMovement() ?: return

        val source = teleportation.source
        val destination = teleportation.destination

        val destPixelX = destination.x * TILE_SIZE
        val destPixelY = destination.y * TILE_SIZE

        baseSprite.setPosition(destPixelX, destPixelY)
        transform.position.set(destination.x, destination.y)

        transform.revertMovementType()
        transform.stopMoving()
    }

    companion object {
        val FAMILY = allOf(BaseSprite::class, Transformable::class).get()
    }
}