package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Queue
import io.pokesync.core.message.Message
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.ui.element.chatbox.ChatMessage
import io.pokesync.core.game.ui.element.chatbox.ChatboxWindow
import io.pokesync.core.game.world.message.MessageListener
import io.pokesync.core.message.DisplayChatMessage
import io.pokesync.core.message.MoveOrthographicCamera
import io.pokesync.core.message.SwitchToChatChannel

/**
 * An entity system that provides chatting capabilities.
 * @author Sino
 */
class ChatSystem(val window: ChatboxWindow, val dispatcher: MessageDispatcher) : EntitySystem() {
    /**
     * The queue of chat related commands.
     */
    private val commands = Queue<Message>()

    /**
     * A [MessageListener] listening for [DisplayChatMessage] messages.
     */
    private val displayChatMsgListener = object : MessageListener<DisplayChatMessage> {
        override fun handle(c: DisplayChatMessage) {
            queueCommand(c)
        }
    }

    /**
     * A [MessageListener] listening for [SwitchToChatChannel] messages.
     */
    private val switchToChannelListener = object : MessageListener<SwitchToChatChannel> {
        override fun handle(c: SwitchToChatChannel) {
            queueCommand(c)
        }
    }

    override fun addedToEngine(engine: Engine) {
        dispatcher.subscribe(DisplayChatMessage::class.java, displayChatMsgListener)
        dispatcher.subscribe(SwitchToChatChannel::class.java, switchToChannelListener)
    }

    override fun removedFromEngine(engine: Engine) {
        dispatcher.unsubscribe(DisplayChatMessage::class.java, displayChatMsgListener)
        dispatcher.unsubscribe(SwitchToChatChannel::class.java, switchToChannelListener)
    }

    override fun update(deltaTime: Float) {
        while (!commands.isEmpty) {
            when (val command = commands.removeFirst()) {
                is SwitchToChatChannel -> {
                    window.switchToChannel(command.channelId)
                }

                is DisplayChatMessage -> {
                    window.appendMessage(command.channelId, ChatMessage(command.displayName, command.text))
                }
            }
        }
    }

    /**
     * Buffers the given [Message] for processing.
     */
    private fun queueCommand(message: Message) {
        commands.addLast(message)
    }
}