package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import io.pokesync.core.account.UserGroup

/**
 * A rank component.
 * @author Sino
 */
data class Rank(val userGroup: UserGroup) : Component