package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Queue
import io.pokesync.core.game.model.DisplayName
import io.pokesync.core.game.ui.element.BaseLayout
import io.pokesync.core.game.ui.element.hud.UserTag
import io.pokesync.core.game.world.component.BaseSprite
import io.pokesync.core.game.world.component.Transformable
import io.pokesync.core.game.world.component.Username
import io.pokesync.core.message.Message
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.world.message.MessageListener
import io.pokesync.core.game.world.tile.TILE_SIZE
import io.pokesync.core.message.*
import ktx.ashley.get

/**
 * An [EntitySystem] that processes requested changes to make to the hud.
 * @author Sino
 */
class HudSystem(val dispatcher: MessageDispatcher, val camera: OrthographicCamera, val baseLayout: BaseLayout) :
    EntitySystem() {
    /**
     * A [MessageListener] listening for [SetPokeDollars] messages.
     */
    private val pokedollarUpdateListener = object : MessageListener<SetPokeDollars> {
        override fun handle(c: SetPokeDollars) {
            queueCommand(c)
        }
    }

    /**
     * A [MessageListener] listening for [SetDonatorPoints] messages.
     */
    private val setDonatorPointsListener = object : MessageListener<SetDonatorPoints> {
        override fun handle(c: SetDonatorPoints) {
            queueCommand(c)
        }
    }

    /**
     * A [MessageListener] listening for [SetServerTime] messages.
     */
    private val setServerTimeListener = object : MessageListener<SetServerTime> {
        override fun handle(c: SetServerTime) {
            queueCommand(c)
        }
    }

    /**
     * A [MessageListener] listening for [ShowDialogue] messages.
     */
    private val showDialogueListener = object : MessageListener<ShowDialogue> {
        override fun handle(c: ShowDialogue) {
            queueCommand(c)
        }
    }

    /**
     * A [MessageListener] listening for [CloseDialogue] messages.
     */
    private val closeDialogueListener = object : MessageListener<CloseDialogue> {
        override fun handle(c: CloseDialogue) {
            queueCommand(c)
        }
    }

    /**
     * The queue of hud update commands.
     */
    private val commands = Queue<Message>()

    /**
     * The list of entities that have user tags.
     */
    private val entitiesWithUserTags = mutableListOf<Entity>()

    /**
     * An [EntityListener] that adds a [UserTag] to entities that have
     * a [Username] component.
     */
    private val userTagSubscriber = object : EntityListener {
        override fun entityAdded(entity: Entity) {
            val username = entity.get<Username>()
            if (username != null) {
                username.tag = baseLayout.createUserTag(username.displayName)

                entitiesWithUserTags.add(entity)
                baseLayout.addActor(username.tag)
            }
        }

        override fun entityRemoved(entity: Entity) {
            val username = entity.get<Username>()
            if (username != null) {
                username.tag!!.remove()
                username.tag = null

                entitiesWithUserTags.remove(entity)
            }
        }
    }

    override fun addedToEngine(engine: Engine) {
        dispatcher.subscribe(SetPokeDollars::class.java, pokedollarUpdateListener)
        dispatcher.subscribe(SetDonatorPoints::class.java, setDonatorPointsListener)
        dispatcher.subscribe(SetServerTime::class.java, setServerTimeListener)
        dispatcher.subscribe(ShowDialogue::class.java, showDialogueListener)
        dispatcher.subscribe(CloseDialogue::class.java, closeDialogueListener)

        engine.addEntityListener(userTagSubscriber)
    }

    override fun removedFromEngine(engine: Engine) {
        dispatcher.unsubscribe(SetPokeDollars::class.java, pokedollarUpdateListener)
        dispatcher.unsubscribe(SetDonatorPoints::class.java, setDonatorPointsListener)
        dispatcher.unsubscribe(SetServerTime::class.java, setServerTimeListener)
        dispatcher.unsubscribe(ShowDialogue::class.java, showDialogueListener)
        dispatcher.unsubscribe(CloseDialogue::class.java, closeDialogueListener)

        engine.removeEntityListener(userTagSubscriber)
    }

    override fun update(deltaTime: Float) {
        while (!commands.isEmpty) {
            when (val command = commands.removeFirst()) {
                is SetPokeDollars -> {
                    baseLayout.hud.setPokeDollars(command.dollars)
                }

                is SetDonatorPoints -> {
                    baseLayout.hud.setDonatorPoints(command.points)
                }

                is SetServerTime -> {
                    baseLayout.hud.setTime(command.hours, command.minutes)
                }

                is ShowDialogue -> {
                    // TODO figure out how to pinpoint where the dialogue should appear

//                    val npcTransform = npc.get<Transformable>()!!
//
//                    val xDir = npcTransform.position.x - transform.position.x
//                    val zDir = npcTransform.position.y - transform.position.y
//                    when {
//                        xDir < 0 -> npcTransform.face(Direction.EAST)
//                        xDir > 0 -> npcTransform.face(Direction.WEST)
//                        zDir < 0 -> npcTransform.face(Direction.NORTH)
//                        zDir > 0 -> npcTransform.face(Direction.SOUTH)
//                    }
//
//                    val clickedCameraX = npcPosition.x * TILE_SIZE
//                    val clickedCameraY = (npcPosition.y + 1) * TILE_SIZE
//
//                    dialogue.screenPoint.set(clickedCameraX, clickedCameraY, 0F)
//                    camera.project(dialogue.screenPoint)
//
//                    val menuScreenX = dialogue.screenPoint.x - (dialogue.width / 4F)
//                    val menuScreenY = dialogue.screenPoint.y
//
//                    dialogue.setPosition(menuScreenX, menuScreenY)
//                    dialogue.toFront()
//
//                    dialogue.prepareTextToDisplay("Let me guess, you want me to teach one of your Pokemon a move? Well then. Too bad for you, haha!")
//                    dialogue.isVisible = true
                }

                is CloseDialogue -> {
                    baseLayout.dialogue.isVisible = false
                }
            }
        }

        for (index in 0 until entitiesWithUserTags.size) {
            updateNameTag(entitiesWithUserTags[index])
        }

        val dialogue = baseLayout.dialogue
        if (dialogue.isVisible && !dialogue.finishedPrinting()) {
            dialogue.addTimePassed(deltaTime)

            if (dialogue.timeToAdvance()) {
                dialogue.clearTimePassing()
                dialogue.advanceCharacter()
            }
        }
    }

    /**
     * Updates the position of the name tag of the given player.
     */
    private fun updateNameTag(player: Entity) {
        val transform = player.get<Transformable>()!!
        val username = player.get<Username>()!!

        val nameTag = username.tag!!

        val worldCameraX = (transform.position.x + 0.2F) * TILE_SIZE
        val worldCameraZ = (transform.position.y + 0.75F) * TILE_SIZE

        nameTag.screenPoint.set(worldCameraX, worldCameraZ, 0F)
        camera.project(nameTag.screenPoint)

        val tagScreenX = nameTag.screenPoint.x + (TILE_SIZE - nameTag.glyphLayout.width) / 2F
        val tagScreenY = nameTag.screenPoint.y + (TILE_SIZE + nameTag.glyphLayout.height) / 2F

        nameTag.setPosition(tagScreenX, tagScreenY)
    }

    /**
     * Buffers the given [Message] for processing.
     */
    private fun queueCommand(message: Message) {
        commands.addLast(message)
    }
}