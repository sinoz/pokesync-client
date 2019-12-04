package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import io.pokesync.core.assets.config.ObjectConfig
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference

/**
 * An [AsynchronousAssetLoader] implementation to load [ObjectConfig]s using LibGDX's [AssetManager].
 * @author Sino
 */
class ObjectConfigLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<ObjectConfig, ObjectConfigLoader.Parameters>(resolver) {
    class Parameters(val directory: Path) : AssetLoaderParameters<ObjectConfig>()

    private val imageRef = AtomicReference<ObjectConfig?>(null)

    override fun loadSync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: Parameters?
    ): ObjectConfig {
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
        imageRef.set(ObjectConfig.load(parameter.directory).unsafeRunSync())
    }
}