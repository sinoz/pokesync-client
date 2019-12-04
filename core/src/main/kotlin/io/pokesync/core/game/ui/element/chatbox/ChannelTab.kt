package io.pokesync.core.game.ui.element.chatbox

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import ktx.style.*

/**
 * The tab button of a chat channel.
 * @author Sino
 */
class ChannelTab(val id: Int, name: String, skin: Skin, pressed: (Int) -> Unit) :
    TextButton(name, skin, "inactive-chat-tab") {
    /**
     * The history of chat messages of a single chat tab.
     */
    class History(val messages: MutableList<ChatMessage>) {
        constructor() : this(mutableListOf())

        fun append(message: ChatMessage) {
            messages.add(message)
        }
    }

    private var isActive = false

    private val history = History()

    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                pressed(id)
            }
        })

        setAsInactive()
    }

    fun appendMessage(message: ChatMessage) {
        history.append(message)
    }

    fun getMessages(): List<ChatMessage> =
        history.messages

    fun setAsActive() {
        style = skin["active-chat-tab"]
        isActive = true
    }

    fun setAsInactive() {
        style = skin["inactive-chat-tab"]
        isActive = false
    }
}