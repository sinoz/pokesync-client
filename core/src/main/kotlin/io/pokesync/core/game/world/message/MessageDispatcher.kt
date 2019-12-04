package io.pokesync.core.game.world.message

/**
 * Forwards commands to their subscribers in a fire-and forget fashion.
 * @author Sino
 */
interface MessageDispatcher {
    /**
     * Subscribes the given [MessageListener] to react to the specified command of type [C].
     */
    fun <C : Any> subscribe(commandType: Class<out C>, listener: MessageListener<C>)

    /**
     * An auxiliary function for [subscribe] using a function type instead.
     */
    fun <C : Any> subscribe(commandType: Class<out C>, f: (C) -> Unit) {
        subscribe(commandType, object : MessageListener<C> {
            override fun handle(c: C) {
                f(c)
            }
        })
    }

    /**
     * Unsubscribes the given [MessageListener] from the specified command of type [C].
     */
    fun <C : Any> unsubscribe(commandType: Class<out C>, listener: MessageListener<C>)

    /**
     * Collapses a topic of the specified command type of [C].
     */
    fun <C : Any> collapse(commandType: Class<out C>)

    /**
     * Publishes the given command of type [C].
     */
    fun <C : Any> publish(command: C)
}