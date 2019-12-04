package io.pokesync.core.game.model

import com.badlogic.gdx.math.Vector2

/**
 * A movement from point A to point B defined in [Vector2]s.
 * @author Sino
 */
data class Movement(
    val source: Vector2,
    val destination: Vector2,
    val direction: Direction?
)