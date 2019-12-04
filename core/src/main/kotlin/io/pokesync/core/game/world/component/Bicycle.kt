package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import ktx.ashley.mapperFor

/**
 * A bicycle.
 * @author Sino
 */
class Bicycle : Component {
    companion object {
        val MAPPER = mapperFor<Bicycle>()
    }

    var cycling = false
}