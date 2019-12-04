package io.pokesync.core.game.world.message

/**
 * A type alias for a function that takes a message of type [M]
 * to process some kind of logical and graphical side effect.
 */
interface MessageListener<M : Any> {
    fun handle(c: M)
}