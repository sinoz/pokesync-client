package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import io.pokesync.core.assets.config.ItemConfig
import io.pokesync.core.assets.config.MonsterConfig
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference

/**
 * An [AsynchronousAssetLoader] implementation to load [MonsterConfig]s using LibGDX's [AssetManager].
 * @author Sino
 */
class ItemConfigLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<ItemConfig, ItemConfigParameter>(resolver) {
    private val imageRef = AtomicReference<ItemConfig?>(null)

    override fun loadSync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: ItemConfigParameter?
    ): ItemConfig {
        val loadedImage = imageRef.get()!!
        imageRef.set(null)
        return loadedImage
    }

    override fun getDependencies(
        fileName: String,
        file: FileHandle,
        parameter: ItemConfigParameter?
    ): Array<AssetDescriptor<Any>>? =
        null

    override fun loadAsync(
        manager: AssetManager,
        fileName: String,
        fileHandle: FileHandle,
        parameter: ItemConfigParameter
    ) {
        imageRef.set(ItemConfig.load(parameter.directory).unsafeRunSync())
    }
}