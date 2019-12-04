package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import io.pokesync.core.game.world.component.BaseSprite
import io.pokesync.core.game.world.component.CanOpenDoors
import io.pokesync.core.game.world.component.Transformable
import ktx.ashley.allOf
import ktx.ashley.get

/**
 * An entity system that allows player characters to open doors.
 * @author Sino
 */
class DoorSystem(val camera: OrthographicCamera) : IteratingSystem(FAMILY) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val doorOpener = entity.get<CanOpenDoors>()!!
        if (doorOpener.tile != null && !doorOpener.hasOpenedDoor) {
            doorOpener.hasOpenedDoor = true

            openAndEnterDoor(entity)
        }
    }

    /**
     * Opens and enters the door, which initially causes a small delay to allow the
     * door animation to present it being opened. Once complete, the camera is zoomed
     * in onto the player and thus starts the in-door teleportation sequence.
     */
    private fun openAndEnterDoor(entity: Entity) {
        val doorOpener = entity.get<CanOpenDoors>()!!
        val transform = entity.get<Transformable>()!!

        // TODO door animation, wait to complete
        // TODO walk into building, black screen faded in
        // TODO teleport
        // TODO fade-out the black screen
    }

    companion object {
        /**
         * The amount of milliseconds to wait for the door to open.
         */
        const val DOOR_ANIM_DELAY = 350L

        /**
         * The zoom value to aim for when zooming in.
         */
        const val TARGET_ZOOM = 0.6F

        /**
         * The velocity at which the camera is zoomed in at.
         */
        const val ZOOM_VELOCITY = 0.008F

        val FAMILY = allOf(BaseSprite::class, Transformable::class, CanOpenDoors::class).get()
    }
}