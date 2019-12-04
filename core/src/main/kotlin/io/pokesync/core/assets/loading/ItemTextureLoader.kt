package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array
import io.pokesync.core.assets.ItemTexture
import io.pokesync.rom.nitro.ArchiveType
import io.pokesync.core.assets.config.ItemConfig
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.readAsNCGR
import io.pokesync.rom.nitro.readAsNCLR
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap

/**
 * An [AsynchronousAssetLoader] that loads [ItemTexture]s asynchronously into memory.
 * @author Sino
 */
class ItemTextureLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<ItemTexture, ItemTextureParameter>(resolver) {
    private val loadedPixmaps = ConcurrentHashMap<ModelId, Pixmap>()

    override fun loadAsync(
        manager: AssetManager,
        fileName: String?,
        file: FileHandle?,
        parameter: ItemTextureParameter
    ) {
        val config = manager.get(ItemConfig::class.java.simpleName, ItemConfig::class.java)!!

        val archiveDescriptor = AssetDescriptor(
            "archive-${ArchiveType.BAG_ITEMS}",
            Archive::class.java,
            ArchiveParameter(ArchiveType.BAG_ITEMS)
        )
        val archive = manager.get(archiveDescriptor)!!

        val configEntry = config.entries[parameter.id.value]

        val graphicFileId = configEntry.graphicFileId
        val paletteFileId = configEntry.paletteFileId

        val graphicFile = archive.fileSystem.lookupFile(graphicFileId)!!
        val paletteFile = archive.fileSystem.lookupFile(paletteFileId)!!

        val frame = graphicFile.readAsNCGR()
        val palette = paletteFile.readAsNCLR()[0]

        val width = frame.width
        val height = frame.height

        val format = frame.colourFormat
        val drawOrder = frame.drawOrder

        val rawBytes = frame.bytes

        loadedPixmaps[parameter.id] = drawPixmap(width, height, drawOrder, rawBytes, format, palette)
    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: ItemTextureParameter?
    ): ItemTexture {
        val pixmap = loadedPixmaps[parameter!!.id] ?: throw IllegalArgumentException()
        loadedPixmaps.remove(parameter.id)
        return ItemTexture(Texture(pixmap))
    }

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: ItemTextureParameter?
    ): Array<AssetDescriptor<*>> {
        val descriptors = Array<AssetDescriptor<*>>()

        descriptors.add(AssetDescriptor(ItemConfig::class.java.simpleName, ItemConfig::class.java))
        descriptors.add(
            AssetDescriptor(
                "archive-${ArchiveType.BAG_ITEMS}",
                Archive::class.java,
                ArchiveParameter(ArchiveType.BAG_ITEMS)
            )
        )

        return descriptors
    }
}