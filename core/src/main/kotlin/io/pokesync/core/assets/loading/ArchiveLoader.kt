package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import io.pokesync.rom.nitro.ArchiveType
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.Image as NitroImage
import java.util.concurrent.ConcurrentHashMap

/**
 * An [AsynchronousAssetLoader] implementation to load Nitro [Archive]s using LibGDX's [AssetManager].
 * @author Sino
 */
class ArchiveLoader(resolver: FileHandleResolver) : AsynchronousAssetLoader<Archive, ArchiveParameter>(resolver) {
    private val loadedArchives = ConcurrentHashMap<ArchiveType, Archive>()

    override fun loadAsync(manager: AssetManager, fileName: String?, file: FileHandle?, parameter: ArchiveParameter?) {
        val image = manager.get(NitroImage::class.java.simpleName, NitroImage::class.java)
        val archiveFile = image.fileSystem.lookupFile(parameter!!.type.nitroId)!!

        loadedArchives[parameter.type] = Archive.read(archiveFile)
    }

    override fun loadSync(
        manager: AssetManager,
        fileName: String?,
        file: FileHandle?,
        parameter: ArchiveParameter?
    ): Archive {
        return loadedArchives[parameter!!.type]!!
    }

    override fun getDependencies(
        fileName: String,
        file: FileHandle,
        parameter: ArchiveParameter?
    ): Array<AssetDescriptor<*>> {
        val deps = Array<AssetDescriptor<*>>()
        deps.add(AssetDescriptor(NitroImage::class.java.simpleName, NitroImage::class.java))
        return deps
    }
}