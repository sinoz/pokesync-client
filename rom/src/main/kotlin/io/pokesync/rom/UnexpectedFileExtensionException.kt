package io.pokesync.rom

/**
 * An exception to throw when an unexpected file extension was read.
 * @author Sino
 */
data class UnexpectedFileExtensionException(override val message: String) : Exception(message)