package io.pokesync.core.net

import java.net.InetSocketAddress

/**
 * A type of remote server point that the client can connect to.
 * @author Sino
 */
enum class RemoteEndpoint(val address: InetSocketAddress) {
    /**
     * The game service.
     */
    GAME_SERVICE(InetSocketAddress("world1.pokesync.com", 23192))
}