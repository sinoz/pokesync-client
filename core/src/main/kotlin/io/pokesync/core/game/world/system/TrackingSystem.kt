package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Queue
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.core.game.model.MovementType
import io.pokesync.core.game.world.EntityFactory
import io.pokesync.core.game.world.WorldGrid
import io.pokesync.core.game.world.component.BaseSprite
import io.pokesync.core.game.world.component.Bicycle
import io.pokesync.core.game.world.component.HasMotion
import io.pokesync.core.game.world.component.Transformable
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.world.message.MessageListener
import io.pokesync.core.message.EntityUpdate
import ktx.ashley.get

/**
 * An entity system that keeps track of nearby entities in the user's viewport.
 * @author Sino
 */
class TrackingSystem(val dispatcher: MessageDispatcher, val worldGrid: WorldGrid, val entityFactory: EntityFactory) :
    EntitySystem() {
    /**
     * An unbounded queue of updates that are to be encoded and flushed to the server.
     */
    private val updateQueue = Queue<EntityUpdate>()

    /**
     * The list of viewable entities.
     */
    private val viewableEntities = mutableListOf<Entity>()

    /**
     * A [MessageListener] listening for [EntityUpdate] messages.
     */
    private val entityUpdateListener = object : MessageListener<EntityUpdate> {
        override fun handle(c: EntityUpdate) {
            queueUpdate(c)
        }
    }

    override fun addedToEngine(engine: Engine) {
        dispatcher.subscribe(EntityUpdate::class.java, entityUpdateListener)
    }

    override fun removedFromEngine(engine: Engine) {
        dispatcher.unsubscribe(EntityUpdate::class.java, entityUpdateListener)
    }

    override fun update(deltaTime: Float) {
        if (updateQueue.isEmpty) {
            return
        }

        val update = updateQueue.removeFirst()

        val changesForExisting = update.updates
        val additions = update.additions

        check(changesForExisting.size == viewableEntities.size)

        val iterator = viewableEntities.iterator()
        var updateIndex = 0
        while (iterator.hasNext()) {
            val updateToApply = changesForExisting[updateIndex]
            val entityToUpdate = iterator.next()

            when (updateToApply.positionUpdate) {
                is EntityUpdate.PositionUpdate.Removal -> {
                    removeEntityFromEngine(entityToUpdate)
                    iterator.remove()
                }

                is EntityUpdate.PositionUpdate.TakeStep -> {
                    entityToUpdate.get<Transformable>()!!.moveTowards(updateToApply.positionUpdate.direction)
                }

                is EntityUpdate.PositionUpdate.Reface -> {
                    entityToUpdate.get<Transformable>()!!.face(updateToApply.positionUpdate.direction)
                }

                is EntityUpdate.PositionUpdate.Teleport -> {
                    val transform = entityToUpdate.get<Transformable>()!!

                    val mapX = updateToApply.positionUpdate.position.mapX
                    val mapZ = updateToApply.positionUpdate.position.mapZ

                    val localX = updateToApply.positionUpdate.position.localX
                    val localZ = updateToApply.positionUpdate.position.localZ

                    val map = worldGrid.lookupMap(mapX, mapZ)!!

                    val renderX = map.properties["ox"] as Int + localX
                    val renderZ = map.properties["oy"] as Int + localZ

                    transform.teleportTo(renderX, renderZ)
                }

                is EntityUpdate.PositionUpdate.ChangedMovementType -> {
                    applyNewMovementType(entityToUpdate, updateToApply.positionUpdate.movementType)
                }

                is EntityUpdate.PositionUpdate.JustStatusChange -> {
                    // TODO
                }

                else -> {
                    // no operation
                }
            }

            updateIndex++
        }

        for (addition in additions) {
            val entity = createEntityFromAddition(addition)

            addEntityToEngine(entity)
            viewableEntities.add(entity)
        }
    }

    /**
     * Schedules the given [Entity] to be added to the engine.
     */
    private fun addEntityToEngine(entity: Entity) {
        engine.addEntity(entity)
    }

    /**
     * Schedules the given [Entity] to be removed from the engine.
     */
    private fun removeEntityFromEngine(entity: Entity) {
        engine.removeEntity(entity)
    }

    /**
     * Applies new [MovementType] onto the given [Entity].
     */
    private fun applyNewMovementType(entity: Entity, newMovementType: MovementType) {
        val transform = entity.get<Transformable>()!!
        val motion = entity.get<HasMotion>()!!
        val baseSprite = entity.get<BaseSprite>()!!

        val bicycle = entity.get<Bicycle>()

        when (newMovementType) {
            MovementType.WALK -> {
                bicycle?.cycling = false

                motion.setWalkingVelocity()
                baseSprite.setRegion(
                    BaseSprite.getStanceTextureByDirection(
                        baseSprite,
                        transform.facingDirection,
                        newMovementType
                    )
                )
            }

            MovementType.RUN -> {
                motion.setRunningVelocity()
            }

            MovementType.CYCLE -> {
                bicycle?.cycling = true

                motion.setCyclingVelocity()
                baseSprite.setRegion(
                    BaseSprite.getCyclingStanceTextureByDirection(
                        baseSprite,
                        transform.facingDirection
                    )
                )
            }

            else -> {
                // nothing
            }
        }

        transform.changeMovementType(newMovementType)
    }

    /**
     * Creates an [Entity] to add to the game, from the given [EntityUpdate.AdditionUpdate].
     */
    private fun createEntityFromAddition(evt: EntityUpdate.AdditionUpdate) =
        when (evt) {
            is EntityUpdate.AdditionUpdate.AddPlayer -> {
                val plr = entityFactory.createPlayer(
                    evt.pid,
                    evt.status.gender,
                    evt.position,
                    evt.direction,
                    evt.status.displayName,
                    evt.status.userGroup
                )
                applyNewMovementType(plr, evt.movementType)
                plr
            }

            is EntityUpdate.AdditionUpdate.AddNpc ->
                entityFactory.createNpc(evt.pid, evt.status.id, evt.position, evt.direction)

            is EntityUpdate.AdditionUpdate.AddMonster ->
                entityFactory.createMonster(
                    evt.pid,
                    evt.status.id,
                    evt.status.coloration,
                    evt.position,
                    evt.direction
                )

            else -> {
                throw Exception()
            }
        }

    private fun queueUpdate(update: EntityUpdate) {
        updateQueue.addLast(update)
    }
}