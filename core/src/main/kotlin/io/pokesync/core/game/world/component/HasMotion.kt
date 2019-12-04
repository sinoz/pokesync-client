package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import ktx.ashley.mapperFor

/**
 * Describes the velocity an entity may traverse the game world at. Without
 * this component, an entity cannot interpolate from one tile to another.
 *
 * The velocity defines how long it may take in time for an entity to take a
 * step on the game map. A velocity of four steps divides a second by four
 * which means that a step may take an approximate of 250 milliseconds.
 * @author Sino
 */
class HasMotion private constructor(private var velocity: Float, private var previousVelocity: Float) : Component {
    companion object {
        val MAPPER = mapperFor<HasMotion>()

        const val WALKING_VELOCITY = 4F
        const val RUNNING_VELOCITY = 6F
        const val SURFING_VELOCITY = 8F
        const val JUMPING_VELOCITY = 8F
        const val CYCLING_VELOCITY = 10F
    }

    constructor() : this(WALKING_VELOCITY, WALKING_VELOCITY)

    fun setWalkingVelocity() {
        previousVelocity = velocity
        velocity = WALKING_VELOCITY
    }

    fun setRunningVelocity() {
        previousVelocity = velocity
        velocity = RUNNING_VELOCITY
    }

    fun setCyclingVelocity() {
        previousVelocity = velocity
        velocity = CYCLING_VELOCITY
    }

    fun setSurfingVelocity() {
        previousVelocity = velocity
        velocity = SURFING_VELOCITY
    }

    fun setJumpingVelocity() {
        previousVelocity = velocity
        velocity = JUMPING_VELOCITY
    }

    fun revertVelocity() {
        velocity = previousVelocity
    }

    fun getVelocity(): Float = velocity
}