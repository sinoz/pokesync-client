package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import io.pokesync.core.net.CanConnectToRemote
import ktx.ashley.mapperFor

/**
 * A [Component] that provides access to networking.
 * @author Sino
 */
class Networked(val client: CanConnectToRemote) : Component {
    companion object {
        val MAPPER = mapperFor<Networked>()
    }
}