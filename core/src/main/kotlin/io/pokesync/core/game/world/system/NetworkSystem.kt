package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.Queue
import io.pokesync.core.game.world.component.Networked
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.world.message.MessageListener
import io.pokesync.core.game.world.message.SendCommandAcrossWire
import io.pokesync.core.message.Message
import io.pokesync.core.net.CanConnectToRemote
import ktx.ashley.allOf
import ktx.ashley.get

/**
 * An entity system that provides networking capabilities. All commands received from the server
 * are published to the given [MessageDispatcher].
 * @author Sino
 */
class NetworkSystem(val dispatcher: MessageDispatcher) : IteratingSystem(FAMILY) {
    /**
     * An unbounded queue of [Message]s that are to be encoded and flushed to the server.
     */
    private val outgoingCommands = Queue<Message>()

    /**
     * A [MessageListener] listening for [SendCommandAcrossWire] messages.
     */
    private val sendCommandAcrossWireListener = object : MessageListener<SendCommandAcrossWire> {
        override fun handle(c: SendCommandAcrossWire) {
            bufferOutgoingCommand(c)
        }
    }

    override fun addedToEngine(engine: Engine) {
        dispatcher.subscribe(SendCommandAcrossWire::class.java, sendCommandAcrossWireListener)

        super.addedToEngine(engine)
    }

    override fun removedFromEngine(engine: Engine) {
        dispatcher.unsubscribe(SendCommandAcrossWire::class.java, sendCommandAcrossWireListener)

        super.removedFromEngine(engine)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val network = entity.get<Networked>()!!

        // TODO
//        if (!network.client.isConnected()) {
//            dispatcher.publish(DisconnectedFromServer)
//            engine.removeEntity(entity)
//
//            return
//        }

        pullCommands(network.client)
        pushCommands(network.client)
    }

    private fun pullCommands(client: CanConnectToRemote) {
        while (true) {
            val message = client.poll() ?: break
            dispatcher.publish(message)
        }
    }

    private fun pushCommands(client: CanConnectToRemote) {
        val queueSize = outgoingCommands.size

        while (outgoingCommands.notEmpty()) {
            client.send(outgoingCommands.removeFirst() ?: break, immediateFlush = false)
        }

        if (queueSize > 0) {
            client.flush()
        }
    }

    private fun bufferOutgoingCommand(command: SendCommandAcrossWire) {
        outgoingCommands.addLast(command.payload)
    }

    companion object {
        val FAMILY = allOf(Networked::class).get()!!
    }
}