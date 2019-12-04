package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import ktx.ashley.mapperFor

/**
 * Stores an entity's items.
 * @author Sino
 */
class ItemBag : Component {
    companion object {
        val MAPPER = mapperFor<ItemBag>()
    }
}