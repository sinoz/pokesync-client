package io.pokesync.core.game.input

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.model.MapPosition
import io.pokesync.core.game.model.MovementType
import io.pokesync.core.game.ui.element.BaseLayout
import io.pokesync.core.game.world.WorldGrid
import io.pokesync.core.game.world.component.*
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.world.message.SendCommandAcrossWire
import io.pokesync.core.game.world.tile.TILE_SIZE
import io.pokesync.core.message.ChangeMovementType
import io.pokesync.core.message.ClickTeleport
import io.pokesync.core.message.FaceDirection
import ktx.ashley.get

/**
 * An input listener for the user to control their character.
 * @author Sino
 */
class WorldInputListener(
    val worldGrid: WorldGrid,
    val avatar: Entity,
    val camera: OrthographicCamera,
    val baseLayout: BaseLayout,
    val dispatcher: MessageDispatcher
) : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.MIDDLE) {
            camera.zoom = 0.5F
        }

        // you cannot move around when you're still in a dialogue
        if (Gdx.input.isKeyPressed(Input.Keys.TAB) && !baseLayout.dialogue.isVisible) {
            val cameraPoint = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F))
            val worldPoint = cameraPoint.set(cameraPoint.x / TILE_SIZE, cameraPoint.y / TILE_SIZE, 0F)

            val worldX = worldPoint.x
            val worldZ = worldPoint.y

            val mapPosition = MapPosition.fromWorldCoordinates(Vector2(worldX, worldZ), worldGrid)

            dispatcher.publish(SendCommandAcrossWire(ClickTeleport(mapPosition)))

            return true
        }

        return false
    }

    override fun keyDown(keyCode: Int): Boolean {
        return when (keyCode) {
            Input.Keys.NUM_1 -> {
                camera.zoom -= ZOOM
                true
            }

            Input.Keys.NUM_2 -> {
                camera.zoom += ZOOM
                true
            }

            else -> {
                handleInput(keyCode, true)
            }
        }
    }

    override fun keyUp(keyCode: Int): Boolean {
        return handleInput(keyCode, false)
    }

    override fun scrolled(amount: Int): Boolean {
        camera.zoom += (ZOOM * amount)
        return true
    }

    private fun handleInput(keyCode: Int, down: Boolean): Boolean {
        // you cannot move around when you're still in a dialogue
        val dialogue = baseLayout.dialogue
        if (dialogue.isVisible) {
            return false
        }

        val transform = avatar.get<Transformable>()!!
        val motion = avatar.get<HasMotion>()!!

        val baseSprite = avatar.get<BaseSprite>()!!
        val bicycle = avatar.get<Bicycle>()!!

        if (!down) {
            transform.setCollided(false)
        }

        // cannot run while cycling. must first get off the bicycle
        if (!bicycle.cycling) {
            if (keyCode == Input.Keys.SHIFT_LEFT) {
                if (down) {
                    transform.changeMovementType(MovementType.RUN)
                    motion.setRunningVelocity()
                } else {
                    transform.changeMovementType(MovementType.WALK)
                    motion.setWalkingVelocity()
                }

                dispatcher.publish(SendCommandAcrossWire(ChangeMovementType(transform.getMovementType())))
            }
        }

        // check if the user has tapped the control left key pad
        if (down && keyCode == Input.Keys.CONTROL_LEFT) {
            bicycle.cycling = !bicycle.cycling
            if (bicycle.cycling) {
                transform.changeMovementType(MovementType.CYCLE)
                baseSprite.setRegion(
                    BaseSprite.getCyclingStanceTextureByDirection(
                        baseSprite,
                        transform.facingDirection
                    )
                )

                motion.setCyclingVelocity()
            } else {
                transform.changeMovementType(MovementType.WALK)
                baseSprite.setRegion(
                    BaseSprite.getStanceTextureByDirection(
                        baseSprite,
                        transform.facingDirection,
                        transform.getMovementType()
                    )
                )

                motion.setWalkingVelocity()
            }

            dispatcher.publish(SendCommandAcrossWire(ChangeMovementType(transform.getMovementType())))
        }

        val inputComponent = InputData.MAPPER[avatar]
        if (inputComponent != null) {
            inputComponent.keyPresses[keyCode] = down
            if (!down) {
                val directionToFace = Direction.getByKeyCode(keyCode)
                if (directionToFace != null) {
                    dispatcher.publish(SendCommandAcrossWire(FaceDirection(directionToFace)))
                    transform.face(directionToFace)
                }

                inputComponent.keyPressTimings[keyCode] = 0F
            }

            return true
        }

        return false
    }

    companion object {
        /**
         * The amount to zoom in or out.
         */
        private const val ZOOM = 0.1F
    }
}