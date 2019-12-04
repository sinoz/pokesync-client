package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import io.pokesync.core.assets.config.AnimSeqConfig
import java.util.concurrent.atomic.AtomicReference

/**
 * An [AsynchronousAssetLoader] implementation to load [AnimSeqConfig]s using LibGDX's [AssetManager].
 * @author Sino
 */
class AnimationConfigLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<AnimSeqConfig, AnimationConfigParameter>(resolver) {
    private val imageRef = AtomicReference<AnimSeqConfig?>(null)

    override fun loadSync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: AnimationConfigParameter?
    ): AnimSeqConfig {
        val loadedImage = imageRef.get()!!
        imageRef.set(null)
        return loadedImage
    }

    override fun getDependencies(
        fileName: String,
        file: FileHandle,
        parameter: AnimationConfigParameter?
    ): Array<AssetDescriptor<Any>>? =
        null

    override fun loadAsync(
        manager: AssetManager,
        fileName: String,
        fileHandle: FileHandle,
        parameter: AnimationConfigParameter
    ) {
        imageRef.set(AnimSeqConfig.load(parameter.directory).unsafeRunSync())
    }
}