package io.pokesync.core.client

/**
 * A build number of the client.
 * @author Sino
 */
data class BuildNumber(
    val major: Int,
    val minor: Int,
    val patch: Int
)