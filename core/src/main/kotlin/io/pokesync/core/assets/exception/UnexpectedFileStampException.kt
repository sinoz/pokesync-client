package io.pokesync.core.assets.exception

/**
 * A [RuntimeException] to throw when there was an unexpected file stamp
 * read from a data file.
 * @author Sino
 */
class UnexpectedFileStampException(val stamp: String) : RuntimeException("Unexpected data file stamp read of $stamp")