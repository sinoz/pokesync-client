package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.model.MovementType
import io.pokesync.core.game.world.component.*
import io.pokesync.core.game.world.component.BaseSprite.Companion.getStanceTextureByDirection
import io.pokesync.core.game.world.component.BaseSprite.Companion.getStepTextureByDirection
import io.pokesync.core.game.world.component.CanJump.Companion.FALL_DOWN
import io.pokesync.core.game.world.component.CanJump.Companion.JUMP_UP
import io.pokesync.core.game.world.tile.TILE_SIZE
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.max
import kotlin.math.min

/**
 * An entity system that provides jumping capabilities.
 * @author Sino
 */
class JumpingSystem : IteratingSystem(FAMILY) {
    override fun processEntity(entity: Entity, delta: Float) {
        val transform = entity.get<Transformable>()!!
        if (transform.getMovement() == null || transform.getMovementType() != MovementType.JUMP) {
            return
        }

        val jump = entity.get<CanJump>()!!
        when (jump.getStage()) {
            JUMP_UP -> jumpUp(entity, delta)
            FALL_DOWN -> fallDown(entity, delta)
        }
    }

    /**
     * Performs the jump-up in the air for the given entity.
     */
    private fun jumpUp(entity: Entity, deltaTime: Float) {
        val baseSprite = entity.get<BaseSprite>()!!
        val transform = entity.get<Transformable>()!!
        val jump = entity.get<CanJump>()!!

        val movement = transform.getMovement()!!

        baseSprite.setRegion(
            getStepTextureByDirection(
                baseSprite,
                movement.direction!!,
                transform.getPreviousMovementType(),
                transform.onLeftStep()
            )
        )

        // TODO improve the jump-up

        val currentX = transform.position.x
        val currentZ = transform.position.y

        val maxZ = movement.source.y + (JUMP_HEIGHT / TILE_SIZE)
        val updatedZ = min(maxZ, currentZ + (JUMP_UP_VELOCITY * deltaTime))

        baseSprite.setPosition(baseSprite.x, updatedZ * TILE_SIZE)
        transform.position.set(currentX, updatedZ)

        if (updatedZ == maxZ) {
            jump.proceedToStage(FALL_DOWN)
        }
    }

    /**
     * Performs the falling down from jumping-up in the air for the given entity.
     */
    private fun fallDown(entity: Entity, deltaTime: Float) {
        val baseSprite = entity.get<BaseSprite>()!!
        val motion = entity.get<HasMotion>()!!
        val transform = entity.get<Transformable>()!!
        val jump = entity.get<CanJump>()!!

        val movement = transform.getMovement()!!

        val currentX = transform.position.x
        val currentY = transform.position.y

        val sourceX = movement.source.x
        val sourceY = movement.source.y

        val destinationX = movement.destination.x
        val destinationY = movement.destination.y

        var newX = currentX
        var newY = currentY

        when (movement.direction) {
            Direction.WEST -> {
                newX = max(destinationX, currentX - (motion.getVelocity() * deltaTime))
            }

            Direction.SOUTH -> {
                newY = max(destinationY, currentY - (motion.getVelocity() * deltaTime))
            }

            Direction.NORTH -> {
                newY = min(destinationY, currentY + (motion.getVelocity() * deltaTime))
            }

            Direction.EAST -> {
                newX = min(destinationX, currentX + (motion.getVelocity() * deltaTime))
            }
        }

        baseSprite.setPosition(newX * TILE_SIZE, newY * TILE_SIZE)
        transform.position.set(newX, newY)

        if (newX == destinationX && newY == destinationY) {
            baseSprite.setRegion(
                getStanceTextureByDirection(
                    baseSprite,
                    movement.direction!!,
                    transform.getPreviousMovementType()
                )
            )

            transform.revertMovementType()
            transform.stopMoving()

            motion.revertVelocity()

            jump.resetStage()
        }
    }

    companion object {
        /**
         * The height an entity is to jump up.
         */
        const val JUMP_HEIGHT = TILE_SIZE / 2F

        /**
         * The velocity at which the entity is to jump up at.
         */
        const val JUMP_UP_VELOCITY = 8F

        val FAMILY = allOf(BaseSprite::class, HasMotion::class, Transformable::class, CanJump::class).get()!!
    }
}