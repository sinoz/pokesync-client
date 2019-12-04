package io.pokesync.core.net

import io.pokesync.core.message.Message

/**
 * A contract for implementations that can connect to a [RemoteEndpoint].
 * @author Sino
 */
interface CanConnectToRemote {
    /**
     * Attempts to connect to the specified [RemoteEndpoint].
     */
    suspend fun connect(endpoint: RemoteEndpoint, timeout: Long): ConnectResponse

    /**
     * Closes the connection, if established.
     */
    suspend fun close()

    /**
     * Waits for a [Message] from the other side. This function suspends
     * if the channel's empty.
     */
    suspend fun receive(): Message

    /**
     * Returns whether a connection has been established and is still active.
     */
    fun isConnected(): Boolean

    /**
     * Attempts to flush queued up bytes to the socket.
     */
    fun flush()

    /**
     * Polls a [Message] from the other side. Returns null if the
     * channel's empty.
     */
    fun poll(): Message?

    /**
     * Attempts to send the given [Message]. May automatically flush the given
     * message and all of its unflushed contents that were written before.
     */
    fun send(message: Message, immediateFlush: Boolean = false)

    companion object {
        /**
         * A null object implementation of [CanConnectToRemote].
         */
        fun voidRemote(): CanConnectToRemote =
            object : CanConnectToRemote {
                override suspend fun connect(endpoint: RemoteEndpoint, timeout: Long): ConnectResponse =
                    ConnectResponse.Ok

                override suspend fun close() {
                    // nothing
                }

                override suspend fun receive(): Message =
                    throw IllegalStateException()

                override fun isConnected(): Boolean =
                    false

                override fun flush() {
                    // nothing
                }

                override fun poll(): Message? =
                    null

                override fun send(message: Message, immediateFlush: Boolean) {
                    // nothing
                }
            }
    }
}