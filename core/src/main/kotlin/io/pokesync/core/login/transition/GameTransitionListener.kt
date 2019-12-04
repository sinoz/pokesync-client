package io.pokesync.core.login.transition

import io.pokesync.core.ScreenStack
import io.pokesync.core.account.UserGroup
import io.pokesync.core.game.GameScreen
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MapPosition
import io.pokesync.core.game.world.component.PID
import io.pokesync.lib.gdx.onRenderingThread
import kotlinx.coroutines.delay

/**
 * Listens for a transition from the login screen to game screen.
 * @author Sino
 */
interface GameTransitionListener {
    /**
     * Transitions into the game screen.
     */
    suspend fun transition(
        pid: PID,
        gender: Gender,
        displayName: DisplayName,
        userGroup: UserGroup,
        position: MapPosition
    )

    companion object {
        /**
         * A default implementation of a [GameTransitionListener].
         */
        fun impl(screenStack: ScreenStack, screenProvider: () -> GameScreen): GameTransitionListener =
            object : GameTransitionListener {
                override suspend fun transition(
                    pid: PID,
                    gender: Gender,
                    displayName: DisplayName,
                    userGroup: UserGroup,
                    position: MapPosition
                ) {
                    // ensuring that the screen mutation doesn't happen in a separate thread
                    onRenderingThread {
                        val gameScreen = screenProvider()

                        screenStack.push(gameScreen) // and set the game screen as the active one

                        gameScreen.attachChatboxDisplayName(displayName)

                        delay(2000)

                        gameScreen.attachAvatar(pid, gender, displayName, userGroup, position)
                    }
                }
            }
    }
}