package io.pokesync.core.net

import java.lang.RuntimeException

/**
 * A [RuntimeException] to throw when a frame is detected as being corrupted
 * and thus cannot be decoded.
 * @author Sino
 */
class CorruptedFrameException(message: String) : RuntimeException(message)