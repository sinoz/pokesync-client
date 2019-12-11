package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Queue
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.core.game.model.StatusCondition
import io.pokesync.core.game.ui.element.party.PartyWindow
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.core.message.Message
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.world.message.MessageListener
import io.pokesync.core.game.world.message.SendCommandAcrossWire
import io.pokesync.core.message.SetPartySlot

/**
 * An entity system that applies changes to the user interface of
 * the user's pokemon party.
 * @author Sino
 */
class PartySystem(val dispatcher: MessageDispatcher, val party: PartyWindow) : EntitySystem() {
    /**
     * The queue of party update commands.
     */
    private val commands = Queue<Message>()

    /**
     * A [MessageListener] listening for [SetPartySlot] messages.
     */
    private val sendCommandAcrossWireListener = object : MessageListener<SetPartySlot> {
        override fun handle(c: SetPartySlot) {
            queueCommand(c)
        }
    }

    override fun addedToEngine(engine: Engine) {
        dispatcher.subscribe(SetPartySlot::class.java, sendCommandAcrossWireListener)
    }

    override fun removedFromEngine(engine: Engine) {
        dispatcher.unsubscribe(SetPartySlot::class.java, sendCommandAcrossWireListener)
    }

    override fun update(deltaTime: Float) {
        while (!commands.isEmpty) {
            when (val command = commands.removeFirst()) {
                is SetPartySlot -> {
                    if (command.monsterId.value == 65535) {
                        party.removeMonsterFromSlot(command.slot)
                    } else {
                        party.attachMonsterToSlot(
                            command.slot,
                            command.monsterId,
                            command.gender,
                            command.coloration,
                            command.statusCondition
                        )
                    }
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