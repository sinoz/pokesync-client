package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Input.Keys.*
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.world.component.InputData
import io.pokesync.core.game.world.component.Transformable
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.world.message.SendCommandAcrossWire
import io.pokesync.core.message.MoveAvatar
import ktx.ashley.allOf
import ktx.ashley.get

/**
 * An entity system that processes user input.
 * @author Sino
 */
class InputSystem(val dispatcher: MessageDispatcher) :
    IteratingSystem(allOf(InputData::class, Transformable::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val input = entity.get<InputData>()!!
        if (input.keyPresses[LEFT]) {
            move(entity, LEFT, deltaTime)
        }

        if (input.keyPresses[RIGHT]) {
            move(entity, RIGHT, deltaTime)
        }

        if (input.keyPresses[UP]) {
            move(entity, UP, deltaTime)
        }

        if (input.keyPresses[DOWN]) {
            move(entity, DOWN, deltaTime)
        }
    }

    private fun move(entity: Entity, keyCode: Int, deltaTime: Float) {
        val input = entity.get<InputData>()!!
        val transform = entity.get<Transformable>()!!

        if (input.keyPresses[keyCode] && !transform.isMoving() && !transform.hasCollided()) {
            input.keyPressTimings[keyCode] += deltaTime
            if (input.keyPressTimings[keyCode] > WALK_THRESHOLD) {
                val directionToMoveTo = Direction.getByKeyCode(keyCode)
                if (directionToMoveTo != null) {
                    dispatcher.publish(SendCommandAcrossWire(MoveAvatar(directionToMoveTo)))
                    transform.moveTowards(directionToMoveTo)
                }
            }

            return
        }
    }

    companion object {
        /**
         * The threshold in seconds at which a key press is no longer considered
         * changing the avatar's sprite orientation, but moving towards a direction.
         */
        private const val WALK_THRESHOLD = 0.2F
    }
}