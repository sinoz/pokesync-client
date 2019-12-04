package io.pokesync.core.game.world.message

/**
 * A [MessageDispatcher] that should only be used in a single threaded environment.
 * All subscribers / listeners are registered in an thread-unsafe dictionary.
 * @author Sino
 */
class SingleThreadedMessageDispatcher : MessageDispatcher {
    private val subscriptions = mutableMapOf<Class<out Any>, MutableList<MessageListener<out Any>>>()

    override fun <C : Any> subscribe(commandType: Class<out C>, listener: MessageListener<C>) {
        var consumerList = subscriptions[commandType]
        if (consumerList == null) {
            consumerList = mutableListOf()
            subscriptions[commandType] = consumerList
        }

        consumerList.add(listener)
    }

    override fun <C : Any> unsubscribe(commandType: Class<out C>, listener: MessageListener<C>) {
        subscriptions[commandType]?.remove(listener)
    }

    override fun <C : Any> collapse(commandType: Class<out C>) {
        subscriptions.remove(commandType)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : Any> publish(command: C) {
        val consumerList = subscriptions[command.javaClass] ?: return
        when {
            consumerList.isEmpty() -> {
                return
            }

            else -> {
                for (i in 0 until consumerList.size) {
                    (consumerList[i] as MessageListener<C>).handle(command)
                }
            }
        }
    }
}