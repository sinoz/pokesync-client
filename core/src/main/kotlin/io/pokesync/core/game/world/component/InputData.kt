package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import ktx.ashley.mapperFor

/**
 * Contains input data.
 * @author Sino
 */
class InputData : Component {
    companion object {
        val MAPPER = mapperFor<InputData>()
    }

    val keyPresses = BooleanArray(256)
    val keyPressTimings = FloatArray(256)
}