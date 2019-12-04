package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Queue
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.model.Direction.*
import io.pokesync.core.game.model.Movement
import io.pokesync.core.game.model.MovementType
import ktx.ashley.mapperFor

/**
 * Stores a world position and allows entities to be re-positioned around the world.
 * @author Sino
 */
class Transformable private constructor(val position: Vector2, var facingDirection: Direction, var mapX: Int, var mapZ: Int) : Component {
    private var prevMovementType = MovementType.WALK

    private var movementType = MovementType.WALK

    private var leftStep = true

    private var movement: Movement? = null

    private val facingQueue = Queue<Direction>()

    private val stepQueue = Queue<Direction>()

    private var collided = false

    /**
     * Changes the [Direction] for the entity to face.
     */
    fun face(direction: Direction) {
        facingDirection = direction
        facingQueue.addLast(direction)
    }

    /**
     * TODO
     */
    fun jumpTo(x: Int, z: Int) { // TODO
        prevMovementType = movementType
        movementType = MovementType.JUMP

        val deltaX = x - position.x
        val deltaZ = z - position.y

        val direction = when {
            deltaX < 0 -> WEST
            deltaX > 0 -> EAST
            deltaZ < 0 -> SOUTH
            deltaZ > 0 -> NORTH
            else       -> null
        }

        movement = Movement(position.cpy(), Vector2(x.toFloat(), z.toFloat()), direction)
    }

    /**
     * Teleports the entity to the specified world coordinates.
     */
    fun teleportTo(x: Int, z: Int) {
        prevMovementType = movementType
        movementType = MovementType.TELEPORT
        movement = Movement(position.cpy(), Vector2(x.toFloat(), z.toFloat()), null)
    }

    /**
     * Queues a step in the given [Direction].
     */
    fun moveTowards(direction: Direction) {
        facingDirection = direction
        stepQueue.addLast(direction)
    }

    /**
     * Sets the [movementType] to the given [newType].
     */
    fun changeMovementType(newType: MovementType) {
        prevMovementType = movementType
        movementType = newType
    }

    /**
     * Reverts the [movementType] to the [prevMovementType].
     */
    fun revertMovementType() {
        movementType = prevMovementType
    }

    /**
     * Returns the previously set movement type.
     */
    fun getPreviousMovementType(): MovementType {
        return movementType
    }

    /**
     * Returns the movement type.
     */
    fun getMovementType(): MovementType {
        return movementType
    }

    /**
     * Returns whether the entity is moving.
     */
    fun isMoving(): Boolean {
        return movement != null
    }

    /**
     * Clears a currently ongoing movement.
     */
    fun stopMoving() {
        movement = null
    }

    /**
     * Sets the [movement].
     */
    fun setMovement(m: Movement) {
        movement = m
    }

    /**
     * Returns a currently ongoing [Movement]. May return null
     * if the entity isn't moving.
     */
    fun getMovement(): Movement? {
        return movement
    }

    /**
     * Polls a [Direction] to take a step towards. May return null
     * if the empty is to stay stationary.
     */
    fun pollStep(): Direction? =
        if (stepQueue.isEmpty)
            null
        else
            stepQueue.removeFirst()

    /**
     * Returns the [Direction] to face.
     */
    fun pollDirectionToFace(): Direction? =
        if (facingQueue.isEmpty)
            null
        else
            facingQueue.removeFirst()

    /**
     * Returns whether the entity is on their left step or not.
     */
    fun onLeftStep(): Boolean {
        return leftStep
    }

    /**
     * Alternates the step.
     */
    fun alternateStep() {
        leftStep = !leftStep
    }

    /**
     * Sets the [collided] flag.
     */
    fun setCollided(flag: Boolean) {
        collided = flag
    }

    /**
     * Returns whether [collided] flag is set.
     */
    fun hasCollided(): Boolean {
        return collided
    }

    companion object {
        /**
         * A [ComponentMapper] to map out a [Transformable] type.
         */
        val MAPPER = mapperFor<Transformable>()

        /**
         * Creates a [Transformable] with the following properties.
         */
        fun at(globalX: Int, globalZ: Int, facing: Direction, mapX: Int, mapZ: Int): Transformable {
            return Transformable(Vector2(globalX.toFloat(), globalZ.toFloat()), facing, mapX, mapZ)
        }
    }
}