package io.pokesync.core.game.input

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.ui.element.BaseLayout
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.core.game.world.component.Kind
import io.pokesync.core.game.world.component.PID
import io.pokesync.core.game.world.component.Transformable
import io.pokesync.core.game.world.getNpcAt
import io.pokesync.core.game.world.getPlayersAt
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.world.message.SendCommandAcrossWire
import io.pokesync.core.game.world.tile.TILE_SIZE
import io.pokesync.core.message.ContinueDialogue
import io.pokesync.core.message.InteractWithEntity
import io.pokesync.core.net.CanConnectToRemote
import ktx.ashley.get

/**
 * An input listener that listens for the user's input to
 * enable interaction with other entities through the UI.
 * @author Sino
 */
class UserInteractionListener(
    val camera: Camera,
    val avatar: Entity,
    val engine: Engine,
    val baseLayout: BaseLayout,
    val dispatcher: MessageDispatcher
) : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val contextMenu = baseLayout.contextMenu
        if (contextMenu.isVisible) {
            // TODO clear id of player currently selected in contextMenu
            contextMenu.isVisible = false
        }

        baseLayout.stage.keyboardFocus = null

        if (button == Input.Buttons.RIGHT) {
            val cameraPoint = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F))
            val worldPoint = cameraPoint.set(cameraPoint.x / TILE_SIZE, cameraPoint.y / TILE_SIZE, 0F)

            val clickedWorldX = worldPoint.x.toInt()
            val clickedWorldZ = worldPoint.y.toInt()

            val players = engine.getPlayersAt(clickedWorldX, clickedWorldZ)
            if (players.size == 1) {
                // you cannot right-click yourself
                if (players[0] == avatar) {
                    return false
                }

                // TODO set id of player currently selected in contextMenu

                val clickedCameraX = clickedWorldX * TILE_SIZE
                val clickedCameraY = (clickedWorldZ + 1) * TILE_SIZE

                contextMenu.screenPoint.set(clickedCameraX, clickedCameraY, 0F)
                camera.project(contextMenu.screenPoint)

                val menuScreenX = contextMenu.screenPoint.x - (contextMenu.width / 4F)
                val menuScreenY = contextMenu.screenPoint.y

                contextMenu.setPosition(menuScreenX, menuScreenY)
                contextMenu.toFront()

                contextMenu.isVisible = true
            }
        }

        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE) {
            baseLayout.togglePauseBackground()
            return true
        }

        val contextMenu = baseLayout.contextMenu
        if (contextMenu.isVisible) {
            contextMenu.isVisible = false
        }

        val dialogue = baseLayout.dialogue
        if (keycode == Input.Keys.Z) {
            if (dialogue.isVisible && !dialogue.finishedPrinting()) {
                dialogue.increaseSpeed()
            } else if (dialogue.isVisible && dialogue.finishedPrinting()) {
                dispatcher.publish(SendCommandAcrossWire(ContinueDialogue))
            } else {
                val transform = avatar.get<Transformable>()!!
                if (!transform.isMoving()) {
                    val facingDirection = transform.facingDirection

                    val npcPosition = transform.position.cpy()
                    when (facingDirection) {
                        Direction.SOUTH -> {
                            npcPosition.add(0F, -1F)
                        }

                        Direction.NORTH -> {
                            npcPosition.add(0F, 1F)
                        }

                        Direction.EAST -> {
                            npcPosition.add(1F, 0F)
                        }

                        Direction.WEST -> {
                            npcPosition.add(-1F, 0F)
                        }
                    }

                    val npc = engine.getNpcAt(npcPosition.x.toInt(), npcPosition.y.toInt())
                    if (npc != null) {
                        val npcTransform = npc.get<Transformable>()!!

                        val xDir = npcTransform.position.x - transform.position.x
                        val zDir = npcTransform.position.y - transform.position.y
                        when {
                            xDir < 0 -> npcTransform.face(Direction.EAST)
                            xDir > 0 -> npcTransform.face(Direction.WEST)
                            zDir < 0 -> npcTransform.face(Direction.NORTH)
                            zDir > 0 -> npcTransform.face(Direction.SOUTH)
                        }

                        dialogue.prepareTextToDisplay("Let me guess, you want me to teach one of your Pokemon a move? Well then. Too bad for you, haha!")

                        val clickedCameraX = npcPosition.x * TILE_SIZE
                        val clickedCameraY = (npcPosition.y + 1) * TILE_SIZE

                        dialogue.screenPoint.set(clickedCameraX, clickedCameraY, 0F)
                        camera.project(dialogue.screenPoint)

                        val menuScreenX = dialogue.screenPoint.x + (TILE_SIZE - dialogue.glyphLayout.width) / 2F
                        val menuScreenY = dialogue.screenPoint.y + (TILE_SIZE + dialogue.glyphLayout.height) / 2F

                        dialogue.setPosition(menuScreenX, menuScreenY)
                        dialogue.toFront()

                        dialogue.isVisible = true

                        dispatcher.publish(SendCommandAcrossWire(InteractWithEntity(npc.get()!!)))
                    }
                }
            }
        }

        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        val dialogue = baseLayout.dialogue
        if (keycode == Input.Keys.Z) {
            if (dialogue.isVisible && !dialogue.finishedPrinting()) {
                dialogue.restoreSpeed()
            }
        }

        return false
    }
}