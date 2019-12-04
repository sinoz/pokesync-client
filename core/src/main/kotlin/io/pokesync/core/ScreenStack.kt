package io.pokesync.core

import java.util.*

/**
 * A last-in-first-out stack of screens.
 * @author Sino
 */
class ScreenStack {
    private val stack = Stack<Screen>()

    /**
     * The [Screen] that is ontop.
     */
    private var onTop: Screen? = null

    /**
     * Pushes the given [Screen] onto the stack.
     */
    fun push(screen: Screen) {
        onTop?.hide()
        stack.push(screen)
        screen.show()
        onTop = screen
    }

    /**
     * Pops the [Screen] that is currently on top.
     */
    fun pop(): Screen? {
        if (isEmpty()) {
            return null
        }

        val screen = stack.pop()
        screen.hide()

        onTop = if (isEmpty()) {
            null
        } else {
            stack.peek()
        }

        return screen
    }

    /**
     * Returns whether this stack is empty or not.
     */
    fun isEmpty(): Boolean =
        stack.isEmpty()

    /**
     * Returns the [Screen] that is currently on top of the stack.
     */
    fun currentlyOnTop(): Screen? =
        onTop
}