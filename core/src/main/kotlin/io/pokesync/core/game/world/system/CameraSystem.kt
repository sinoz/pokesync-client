package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Queue
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.message.MoveOrthographicCamera
import io.pokesync.core.message.ResetOrthographicCamera
import io.pokesync.core.game.world.component.CameraFocused
import io.pokesync.core.game.world.component.BaseSprite
import io.pokesync.core.game.world.message.MessageListener
import io.pokesync.core.message.Message
import ktx.ashley.allOf

/**
 * An [EntitySystem] that has the given [OrthographicCamera] apply focus onto the
 * given avatar [Entity] and follow the avatar around as it moves around the world.
 * @author Sino
 */
class CameraSystem(val camera: OrthographicCamera, val dispatcher: MessageDispatcher) :
    IteratingSystem(allOf(CameraFocused::class).get()) {
    /**
     * The queue of camera update commands.
     */
    private val commands = Queue<Message>()

    /**
     * A [MessageListener] listening for [MoveOrthographicCamera] messages.
     */
    private val moveCameraListener = object : MessageListener<MoveOrthographicCamera> {
        override fun handle(c: MoveOrthographicCamera) {
            queueCommand(c)
        }
    }

    /**
     * A [MessageListener] listening for [ResetOrthographicCamera] messages.
     */
    private val resetCameraListener = object : MessageListener<ResetOrthographicCamera> {
        override fun handle(c: ResetOrthographicCamera) {
            queueCommand(c)
        }
    }

    override fun addedToEngine(engine: Engine) {
        dispatcher.subscribe(MoveOrthographicCamera::class.java, moveCameraListener)
        dispatcher.subscribe(ResetOrthographicCamera::class.java, resetCameraListener)

        super.addedToEngine(engine)
    }

    override fun removedFromEngine(engine: Engine) {
        dispatcher.unsubscribe(MoveOrthographicCamera::class.java, moveCameraListener)
        dispatcher.unsubscribe(ResetOrthographicCamera::class.java, resetCameraListener)

        super.removedFromEngine(engine)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val focus = CameraFocused.MAPPER[entity]

        while (!commands.isEmpty) {
            when (commands.removeFirst()) {
                is MoveOrthographicCamera -> {
                    // TODO

                    focus.enabled = false
                }

                is ResetOrthographicCamera -> {
                    focus.enabled = true
                }
            }
        }

        if (focus.enabled) {
            restoreCameraPosition(entity)
        }

        camera.update()
    }

    /**
     * Restores an entity's camera position.
     */
    private fun restoreCameraPosition(entity: Entity) {
        val sprite = BaseSprite.MAPPER[entity]
        camera.position.set(sprite.x, sprite.y, 0F)
    }

    /**
     * Buffers the given [Message] for processing.
     */
    private fun queueCommand(message: Message) {
        commands.addLast(message)
    }
}