package io.pokesync.lib.gdx

/**
 * Listens for a user's attempt to close the program.
 * @author Sino
 */
interface ProgramCloseListener {
    fun onAttempt(terminate: () -> Unit)
}