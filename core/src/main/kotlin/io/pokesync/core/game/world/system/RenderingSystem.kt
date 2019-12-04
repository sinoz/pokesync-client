package io.pokesync.core.game.world.system

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.Queue
import io.pokesync.core.game.world.message.MessageDispatcher
import io.pokesync.core.game.world.WorldGrid
import io.pokesync.core.game.world.component.*
import io.pokesync.core.game.world.message.MessageListener
import io.pokesync.core.game.world.tile.DecoratedOrthoTiledMapRenderer
import io.pokesync.core.game.world.tile.TILE_SIZE
import io.pokesync.core.message.MapRefreshed
import io.pokesync.core.util.PreferenceSet
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.max

/**
 * An [EntitySystem] that draws the game map and its entities.
 * @author Sino
 */
class RenderingSystem(
    val dispatcher: MessageDispatcher,
    val batch: SpriteBatch,
    val camera: OrthographicCamera,
    val worldGrid: WorldGrid,
    val preferenceSet: PreferenceSet
) : IteratingSystem(AVATAR_FAMILY) {
    /**
     * The collection of renderers for each map.
     */
    private lateinit var renderers: Array<Array<MapRenderer?>>

    /**
     * The small grid of maps that the avatar can roam at once.
     */
    private val viewport = Array<Array<MapRenderer?>>(MAP_WIDTH) { arrayOfNulls(MAP_LENGTH) }

    /**
     * The total collection of entities.
     */
    private val entities = mutableListOf<Entity>()

    /**
     * An unbounded queue of map updates.
     */
    private val updateQueue = Queue<MapRefreshed>()

    /**
     * Listens for [MapRefreshed] events.
     */
    private val mapRefreshListener = object : MessageListener<MapRefreshed> {
        override fun handle(c: MapRefreshed) {
            queueUpdate(c)
        }
    }

    /**
     * An [EntityListener] that listens for entities that belong to the [ENTITY_FAMILY].
     */
    private val drawableEntitySubscriber = object : EntityListener {
        override fun entityAdded(entity: Entity) {
            entities.add(entity)
        }

        override fun entityRemoved(entity: Entity) {
            entities.remove(entity)
        }
    }

    /**
     * Queues the given [MapRefreshed] update.
     */
    private fun queueUpdate(update: MapRefreshed) {
        updateQueue.addLast(update)
    }

    /**
     * Initializes the [renderers].
     */
    private fun initializeMapRenderers() {
        renderers = Array(worldGrid.width()) { arrayOfNulls<MapRenderer?>(worldGrid.length()) }

        for (x in 0 until worldGrid.width()) {
            for (z in 0 until worldGrid.length()) {
                val map = worldGrid.lookupMap(x, z)
                if (map != null) {
                    renderers[x][z] = DecoratedOrthoTiledMapRenderer(
                        OrthogonalTiledMapRenderer(map, TILE_UNIT_SCALE, batch),
                        preferenceSet,
                        ::drawEntities
                    )
                }
            }
        }
    }

    /**
     * Refreshes the viewport to render the maps that lay adjacent from the map
     * that the given entity is currently in.
     */
    private fun refreshAdjacentMaps(entity: Entity, centerMapX: Int, centerMapZ: Int) {
        val transform = entity.get<Transformable>()!!

        val lowerBoundMapX = max(0, centerMapX - SEARCH_MARGIN)
        val lowerboundMapZ = max(0, centerMapZ - SEARCH_MARGIN)

        val upperBoundMapX = centerMapX + SEARCH_MARGIN
        val upperboundMapZ = centerMapZ + SEARCH_MARGIN

        for (x in 0 until MAP_WIDTH) {
            for (z in 0 until MAP_LENGTH) {
                viewport[x][z] = null
            }
        }

        for (x in lowerBoundMapX..upperBoundMapX) {
            for (z in lowerboundMapZ..upperboundMapZ) {
                val renderer = renderers[x][z]
                if (renderer != null) {
                    viewport[x - lowerBoundMapX][z - lowerboundMapZ] = renderer
                }
            }
        }

        transform.mapX = centerMapX
        transform.mapZ = centerMapZ
    }

    /**
     * Sorts entities by their Y coordinate.
     */
    private fun sortEntitiesByYOrder() {
        entities.sortByDescending { BaseSprite.MAPPER[it].y }
    }

    /**
     * Draws entities.
     */
    private fun drawEntities() {
        sortEntitiesByYOrder()

        for (i in 0 until entities.size) {
            val entity = entities[i]

            val baseSprite = entity.get<BaseSprite>()!!
            val transform = entity.get<Transformable>()!!

            // in case we are dealing with a 64 x 64 (legendary monsters) sprite,
            // we slightly adjust its sprite position based on its world position
            // so that it lands perfectly on a tile, allowing it to stay in sync
            // with a player it is following
            val baseWidth = baseSprite.regionWidth
            if (baseWidth > TILE_SIZE) {
                val baseWorldX = transform.position.x
                val basePixelX = baseWorldX * TILE_SIZE

                baseSprite.x = basePixelX - (baseWidth / 4F)
            }

            val shadowSprite = entity.get<Shadow>()
            if (shadowSprite != null) {
                // for large sprites, shadows are also adjusted
                var shadowX = baseSprite.x
                if (baseWidth > TILE_SIZE) {
                    shadowX += (baseWidth / 4F)
                }

                // the shadow just follows the base sprite along
                shadowSprite.setPosition(shadowX, baseSprite.y - 8F)
                shadowSprite.draw(batch)
            }

            baseSprite.draw(batch)
        }
    }

    override fun addedToEngine(engine: Engine) {
        initializeMapRenderers()

        dispatcher.subscribe(MapRefreshed::class.java, mapRefreshListener)
        engine.addEntityListener(ENTITY_FAMILY, drawableEntitySubscriber)

        super.addedToEngine(engine)
    }

    override fun removedFromEngine(engine: Engine) {
        dispatcher.unsubscribe(MapRefreshed::class.java, mapRefreshListener)
        engine.removeEntityListener(drawableEntitySubscriber)

        super.removedFromEngine(engine)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (!updateQueue.isEmpty) {
            val refresh = updateQueue.removeFirst()

            val mapX = refresh.mapX
            val mapZ = refresh.mapZ

            refreshAdjacentMaps(entity, mapX, mapZ)
        }

        batch.projectionMatrix = camera.combined

        for (x in 0 until MAP_WIDTH) {
            for (z in 0 until MAP_LENGTH) {
                val r = viewport[x][z]
                if (r != null) {
                    r.setView(camera)
                    r.render()
                }
            }
        }
    }

    companion object {
        /**
         * A [Family] that defines what components an avatar entity should have.
         */
        val AVATAR_FAMILY = allOf(Transformable::class, CameraFocused::class).get()!!

        /**
         * A [Family] that defines what components a regular entity should have.
         */
        val ENTITY_FAMILY = allOf(Transformable::class, BaseSprite::class).get()!!

        /**
         * The unit scale of tiles.
         */
        const val TILE_UNIT_SCALE = 1F // 1 unit is equal to 1 tile

        /**
         * The width and length of the map view.
         */
        const val MAP_WIDTH = 3
        const val MAP_LENGTH = 3

        /**
         * The amount of maps to seach for adjacently.
         */
        const val SEARCH_MARGIN = 1
    }
}