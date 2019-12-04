package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import ktx.ashley.mapperFor

/**
 * A component that describes that an entity can open doors
 * and thus enter buildings.
 * @author Sino
 */
class CanOpenDoors : Component {
    /**
     * The tile of the door.
     */
    var tile: Vector2? = null

    /**
     * A flag to signify whether an entity has opened the door.
     */
    var hasOpenedDoor = false

    companion object {
        val MAPPER = mapperFor<CanOpenDoors>()
    }
}