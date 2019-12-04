package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.game.ui.element.hud.UserTag
import ktx.ashley.mapperFor

/**
 * A component for a [DisplayName].
 * @author Sino
 */
class Username(val displayName: DisplayName) : Component {
    companion object {
        val MAPPER = mapperFor<Username>()
    }

    var tag: UserTag? = null
}