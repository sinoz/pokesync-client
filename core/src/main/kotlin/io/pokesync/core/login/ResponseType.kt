package io.pokesync.core.login

import io.pokesync.core.message.LoginSuccess

/**
 * A login response.
 * @author Sino
 */
interface ResponseType {
    /**
     * A successful response.
     */
    data class Ok(val message: LoginSuccess) : ResponseType

    /**
     * A response that indicates that there was an issue during
     * the user's login attempt.
     */
    enum class Bad(val message: String) : ResponseType { // TODO update messages
        UNABLE_TO_CONNECT("Unable to connect to the game service."),

        TIMED_OUT("Time out."),

        INVALID_CREDENTIALS("You have entered invalid credentials."),

        WORLD_FULL("The game world is full."),

        ALREADY_LOGGED_IN("Your account was already logged into."),

        ACCOUNT_DISABLED("Your account has been disabled."),

        UNABLE_TO_FETCH_PROFILE("The server was unable to fetch your profile."),

        UNEXPECTED_RESPONSE("Received unexpected server response.")
    }
}