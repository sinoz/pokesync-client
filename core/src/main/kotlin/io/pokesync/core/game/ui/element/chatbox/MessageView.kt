package io.pokesync.core.game.ui.element.chatbox

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.pokesync.core.game.model.DisplayName

/**
 * A view of all chat messages categorized per chat channel.
 * @author Sino
 */
class MessageView(val tabs: List<ChannelTab>, skin: Skin) : Table(skin) {
    /**
     * The currently active chat channel.
     */
    private var activeChannel = -1

    /**
     * Switches to the specified channel, clearing the current view and
     * adding all of the previous broadcasted messages.
     */
    fun switchToChannel(id: Int) {
        require(!(id < 0 || id >= tabs.size))

        activeChannel = id

        clearChildren()

        for (tab in tabs) {
            tab.setAsInactive()
        }

        val targetTab = tabs[id]
        targetTab.setAsActive()

        for (message in targetTab.getMessages()) {
            addMessageLabel(message.sender, message.payload)
        }
    }

    /**
     * Appends the given [ChatMessage] to the specified channel.
     */
    fun append(channelId: Int, message: ChatMessage) {
        require(!(channelId < 0 || channelId >= tabs.size))

        val tab = tabs[channelId]
        tab.appendMessage(message)

        if (activeChannel == channelId) {
            addMessageLabel(message.sender, message.payload)
        }
    }

    /**
     * Returns the currently active channel.
     */
    fun getActiveChannel(): Int =
        activeChannel

    private fun addMessageLabel(sender: DisplayName, payload: String) {
        val message = Label("${sender.str}: $payload", skin, "chat-message")
        message.setWrap(true)

        add(message)
            .expandX()
            .fillX()
            .left()
            .bottom()
            .row()
    }
}