package io.pokesync.core.game.world.tile

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.MapRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.Disposable
import io.pokesync.core.util.PreferenceSet

/**
 * A [MapRenderer] that decorates the given [OrthogonalTiledMapRenderer] to
 * always draw entities on a specific layer. Doing this will fix an issue where
 * entities that are positionally 'standing behind' certain objects, visually
 * aren't doing so.
 * @author Sino
 */
class DecoratedOrthoTiledMapRenderer(
    val renderer: OrthogonalTiledMapRenderer,
    val preferenceSet: PreferenceSet,
    val renderEntities: () -> Unit
) : MapRenderer, Disposable {
    override fun render() {
        beginRender()

        val layerCount = renderer.map.layers.size()
        for (layerId in 0 until layerCount) {
            val tiledLayer = renderer.map.layers[layerId] as TiledMapTileLayer
            if (layerId == ENTITY_LAYER) {
                renderEntities()
            } else {
                if (layerId == COLLISION_LAYER && !preferenceSet.renderCollisionFlags) {
                    continue
                }

                renderer.renderTileLayer(tiledLayer)
            }
        }

        endRender()
    }

    override fun render(layers: IntArray) {
        beginRender()

        for (layerId in layers) {
            val tiledLayer = renderer.map.layers[layerId] as TiledMapTileLayer
            if (layerId == ENTITY_LAYER) {
                renderEntities()
            } else {
                renderer.renderTileLayer(tiledLayer)
            }
        }

        endRender()
    }

    override fun setView(camera: OrthographicCamera) {
        renderer.setView(camera)
    }

    override fun setView(
        projectionMatrix: Matrix4,
        viewboundsX: Float,
        viewboundsY: Float,
        viewboundsWidth: Float,
        viewboundsHeight: Float
    ) {
        renderer.setView(projectionMatrix, viewboundsX, viewboundsY, viewboundsWidth, viewboundsHeight)
    }

    private fun beginRender() {
        AnimatedTiledMapTile.updateAnimationBaseTime()
        renderer.batch.begin()
    }

    private fun endRender() {
        renderer.batch.end()
    }

    override fun dispose() {
        renderer.dispose()
    }
}