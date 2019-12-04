package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import io.pokesync.core.assets.config.WorldConfig
import io.pokesync.core.game.world.WorldGrid

/**
 * An [AsynchronousAssetLoader] implementation to load [WorldGrid]s using LibGDX's [AssetManager].
 * @author Sino
 */
// TODO kept synchronous until WorldGrid loads graphic and collision data from binary files
//      the TmxMapLoader() requires an OpenGL context and can thus not be loaded async
class WorldGridLoader(resolver: FileHandleResolver) :
    SynchronousAssetLoader<WorldGrid, WorldGridLoader.Parameters>(resolver) {
    class Parameters : AssetLoaderParameters<WorldGrid>()

    override fun load(
        assetManager: AssetManager,
        fileName: String?,
        file: FileHandle,
        parameter: Parameters?
    ): WorldGrid {
        val worldConfig = assetManager.get(WorldConfig::class.java.simpleName, WorldConfig::class.java)
        val worldGrid = WorldGrid.fromConfig(worldConfig).unsafeRunSync()

        return worldGrid
    }

    override fun getDependencies(
        fileName: String,
        file: FileHandle,
        parameter: Parameters?
    ): Array<AssetDescriptor<*>> {
        val deps = Array<AssetDescriptor<*>>()
        deps.add(AssetDescriptor(WorldConfig::class.java.simpleName, WorldConfig::class.java))
        return deps
    }
}