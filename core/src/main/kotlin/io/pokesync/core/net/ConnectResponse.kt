package io.pokesync.core.net

/**
 * These are responses returned by attempts to connect to a [RemoteEndpoint].
 * @author Sino
 */
sealed class ConnectResponse {
    object Ok : ConnectResponse()
    object Refused : ConnectResponse()
    object TimedOut : ConnectResponse()

    data class Otherwise(val error: Throwable) : ConnectResponse()
}