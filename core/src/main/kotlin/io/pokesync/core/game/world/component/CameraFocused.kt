package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import ktx.ashley.mapperFor

/**
 * A component to have the sprite being followed and focused on by
 * an orthographic camera.
 * @author Sino
 */
class CameraFocused : Component {
    companion object {
        val MAPPER = mapperFor<CameraFocused>()
    }

    /**
     * Defines whether focus should be applied or allow the camera to
     * move away from the user's avatar.
     */
    var enabled = true
}