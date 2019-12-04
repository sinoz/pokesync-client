package io.pokesync.core.assets.loading

import arrow.effects.IO
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import java.util.concurrent.atomic.AtomicReference
import io.pokesync.rom.nitro.Image as NitroImage

/**
 * An [AsynchronousAssetLoader] implementation to load [NitroImage]s using LibGDX's [AssetManager].
 * @author Sino
 */
class NitroImageLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<NitroImage, NitroImageParameter>(resolver) {
    private val imageRef = AtomicReference<NitroImage?>(null)

    override fun loadSync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: NitroImageParameter?
    ): NitroImage {
        val loadedImage = imageRef.get()!!
        imageRef.set(null)
        return loadedImage
    }

    override fun getDependencies(
        fileName: String,
        file: FileHandle,
        parameter: NitroImageParameter?
    ): Array<AssetDescriptor<Any>>? =
        null

    override fun loadAsync(
        manager: AssetManager,
        fileName: String,
        fileHandle: FileHandle,
        parameter: NitroImageParameter
    ) {
        imageRef.set(DiskFile
            .findBy(parameter.directory, DiskFile.Companion::ofNdsExtension)
            .bind { IO { NitroImage.create(it) } }
            .unsafeRunSync())
    }
}