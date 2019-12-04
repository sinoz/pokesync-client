package io.pokesync.core.game.ui.element.chatbox

import com.badlogic.gdx.scenes.scene2d.ui.*
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.game.ui.element.CloseWindowButton
import io.pokesync.core.game.ui.element.chatbox.command.Command
import io.pokesync.core.game.ui.element.chatbox.command.CommandListener
import io.pokesync.core.game.ui.element.chatbox.command.CommandListenerRepository
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.world.message.SendCommandAcrossWire
import io.pokesync.core.message.SelectChatChannel
import io.pokesync.core.message.SubmitChatCommand
import io.pokesync.core.message.SubmitChatMessage
import io.pokesync.core.net.CanConnectToRemote

/**
 * The window of the chatbox.
 * @author Sino
 */
class ChatboxWindow(skin: Skin) : Window("Chatbox", skin, "chat-window") {
    /**
     * Listens for user input related to chat.
     */
    interface ChatListener {
        /**
         * Reacts to the given [ChatMessage] having been submitted.
         */
        fun messageSubmitted(message: ChatMessage)

        /**
         * Reacts to the specified channel id having been selected.
         */
        fun channelSelected(id: Int)

        /**
         * Reacts to the given chat [Command] having been entered.
         */
        fun commandEntered(command: Command)

        companion object {
            /**
             * A [ChatListener] that publishes [ChatMessage]s.
             */
            fun dispatched(dispatcher: MessageDispatcher): ChatListener =
                object : ChatListener {
                    override fun channelSelected(id: Int) {
                        dispatcher.publish(SendCommandAcrossWire(SelectChatChannel(id)))
                    }

                    override fun messageSubmitted(message: ChatMessage) {
                        dispatcher.publish(SendCommandAcrossWire(SubmitChatMessage(message.payload)))
                    }

                    override fun commandEntered(command: Command) {
                        dispatcher.publish(SendCommandAcrossWire(SubmitChatCommand(command.trigger, command.arguments)))
                    }
                }
        }
    }

    private val channelSwitchAction = { channelId: Int ->
        switchToChannel(channelId)
    }

    private val channelTabs = mutableListOf(
        ChannelTab(0, "General", skin, channelSwitchAction),
        ChannelTab(1, "Trading", skin, channelSwitchAction),
        ChannelTab(2, "Guild", skin, channelSwitchAction)
    )

    private val chatListeners = mutableListOf<ChatListener>()

    private val commandRepository = CommandListenerRepository()

    private val displayNameLabel = DisplayNameLabel(skin)

    private val messageInputField = MessageInputField(skin) {
        if (displayNameLabel.isSet()) {
            notifyChatMessageSubmitted(ChatMessage(displayNameLabel.getDisplayName(), it))
        }
    }

    private val submitButton = SubmitButton(skin) {
        if (messageInputField.text.isNotEmpty() && displayNameLabel.isSet()) {
            notifyChatMessageSubmitted(ChatMessage(displayNameLabel.getDisplayName(), messageInputField.text))
            messageInputField.clearText()
        }
    }

    private val topRow = createTopRow(channelTabs)

    private val messageView = MessageView(channelTabs, skin)

    private val scrollPane = ButtonedScrollPane(messageView, skin, "scroll-pane")

    private val bottomRow = createBottomRow()

    init {
        isMovable = true
        isResizable = true

        scrollPane.setForceScroll(false, true)
        scrollPane.setSmoothScrolling(false)
        scrollPane.setScrollBarPositions(false, false)

        titleTable
            .add(CloseWindowButton(skin) { isVisible = false })
            .right()
            .padLeft(1F)
            .padBottom(1F)

        add(topRow)
            .expandX()
            .fillX()
            .top()
            .left()
            .row()

        add(scrollPane)
            .expand()
            .fill()
            .left()
            .bottom()
            .width(300F)
            .height(160F)
            .minWidth(150F)
            .minHeight(150F)
            .row()

        add(Image(skin, "separator"))
            .expandX()
            .fillX()
            .bottom()
            .row()

        add(bottomRow)
            .expandX()
            .fillX()

        pack()
    }

    /**
     * Presents this chatbox.
     */
    fun show() {
        isVisible = true
    }

    /**
     * Hides this chatbox.
     */
    fun hide() {
        isVisible = false
    }

    /**
     * Sets a display name.
     */
    fun setDisplayName(displayName: DisplayName) {
        displayNameLabel.attachUsername(displayName.str)
    }

    /**
     * Adds the given [ChatListener].
     */
    fun addListener(listener: ChatListener) {
        chatListeners.add(listener)
    }

    /**
     * Removes the given [ChatListener].
     */
    fun removeListener(listener: ChatListener) {
        chatListeners.remove(listener)
    }

    /**
     * Subscribes a [CommandListener].
     */
    fun addCommandListener(trigger: String, listener: CommandListener) {
        commandRepository.subscribe(trigger, listener)
    }

    /**
     * Switches to the specified channel.
     */
    fun switchToChannel(id: Int) {
        messageView.switchToChannel(id)

        notifyChatChannelSelected(id)
    }

    /**
     * Appends the given [ChatMessage] to the specified channel.
     */
    fun appendMessage(channelId: Int, message: ChatMessage) {
        messageView.append(channelId, message)
        scrollPane.scrollTo(0F, 0F, 0F, 0F)
    }

    /**
     * Notifies all [chatListeners] that the specified channel id was selected.
     */
    private fun notifyChatChannelSelected(id: Int) {
        chatListeners.forEach { it.channelSelected(id) }
    }

    /**
     * Notifies all [chatListeners] that the given [ChatMessage] was submitted.
     */
    private fun notifyChatMessageSubmitted(message: ChatMessage) {
        val firstTwoCharacters = message.payload.take(2)
        if (firstTwoCharacters != COMMAND_SYMBOLS) {
            chatListeners.forEach { it.messageSubmitted(message) }
        } else {
            val split = message.payload.drop(2).split(" ")

            val trigger = split[0].toLowerCase()
            val arguments = split.drop(1)

            // check if the client has its own listener for commands such as ||gc
            val listener = commandRepository.get(trigger)
            if (listener != null) {
                listener.handle(arguments)
            } else {
                chatListeners.forEach { it.commandEntered(Command(trigger, arguments)) }
            }
        }
    }

    private fun createTopRow(channels: List<ChannelTab>): Table {
        val topRow = Table()

        val channelGroup = HorizontalGroup()
        for (tab in channels) {
            channelGroup.addActor(tab)
        }

        topRow.add(channelGroup).left()
        topRow.row()

        topRow.add(Image(skin, "separator"))
            .colspan(channelTabs.size)
            .expandX()
            .fillX()
            .row()

        topRow.pack()

        return topRow
    }

    private fun createBottomRow(): Table {
        val bottomRow = Table()

        bottomRow
            .add(displayNameLabel)
            .padTop(4F)
            .padLeft(2.5F)
            .left()

        bottomRow
            .add(messageInputField)
            .expandX()
            .fillX()
            .padLeft(3F)
            .padTop(5F)

        bottomRow
            .add(submitButton)
            .padLeft(5F)
            .padTop(5F)
            .right()

        return bottomRow
    }

    companion object {
        /**
         * The symbols to enter to enter a game command.
         */
        const val COMMAND_SYMBOLS = "||"
    }
}