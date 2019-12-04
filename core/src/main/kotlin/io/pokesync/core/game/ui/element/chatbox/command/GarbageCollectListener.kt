package io.pokesync.core.game.ui.element.chatbox.command

/**
 * Stimulates the garbage collector to perform a GC cycle.
 * @author Sino
 */
class GarbageCollectListener : CommandListener {
    override fun handle(arguments: List<String>) {
        System.gc()
    }
}