package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import io.pokesync.core.assets.config.NpcConfig
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference

/**
 * An [AsynchronousAssetLoader] implementation to load [NpcConfig]s using LibGDX's [AssetManager].
 * @author Sino
 */
class NpcConfigLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<NpcConfig, NpcConfigLoader.Parameters>(resolver) {
    class Parameters(val directory: Path) : AssetLoaderParameters<NpcConfig>()

    private val imageRef = AtomicReference<NpcConfig?>(null)

    override fun loadSync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: Parameters?
    ): NpcConfig {
        val loadedImage = imageRef.get()!!
        imageRef.set(null)
        return loadedImage
    }

    override fun getDependencies(
        fileName: String,
        file: FileHandle,
        parameter: Parameters?
    ): Array<AssetDescriptor<Any>>? =
        null

    override fun loadAsync(manager: AssetManager, fileName: String, fileHandle: FileHandle, parameter: Parameters) {
        imageRef.set(NpcConfig.load(parameter.directory).unsafeRunSync())
    }
}