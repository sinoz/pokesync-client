package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array
import io.pokesync.rom.nitro.ArchiveType
import io.pokesync.core.assets.OverworldMonsterTexture
import io.pokesync.core.assets.OverworldNpcTexture
import io.pokesync.core.assets.config.MonsterConfig
import io.pokesync.lib.bytes.unpackBit4
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.readAsBTX0
import java.util.concurrent.ConcurrentHashMap

/**
 * An [AsynchronousAssetLoader] that loads [OverworldNpcTexture]s asynchronously into memory.
 * @author Sino
 */
class OverworldMonsterTextureLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<OverworldMonsterTexture, OverworldMonsterTextureParameter>(resolver) {
    private val loadedPixmaps = ConcurrentHashMap<Int, Pixmap>()

    override fun loadAsync(
        manager: AssetManager,
        fileName: String?,
        fileHandle: FileHandle?,
        parameter: OverworldMonsterTextureParameter
    ) {
        val config = manager.get(MonsterConfig::class.java.simpleName, MonsterConfig::class.java)!!

        val archiveDescriptor = AssetDescriptor(
            "archive-${ArchiveType.OVERWORLD_ENTITIES}", Archive::class.java, ArchiveParameter(
                ArchiveType.OVERWORLD_ENTITIES
            )
        )
        val archive = manager.get(archiveDescriptor)!!

        val entry = config.entries[parameter.id.value]
        val overworldFileId = entry.overworldFileId

        val file = archive.fileSystem.lookupFile(overworldFileId)!!
        val tex = file.readAsBTX0()

        val sheetWidth = tex.frames[0].width
        val sheetHeight = tex.frames[0].width * tex.frames.size

        val sheet = Pixmap(sheetWidth, sheetHeight, Pixmap.Format.RGBA8888)
        var y = 0

        for (textureFrame in tex.frames) {
            val palette = tex.palettes[parameter.coloration.ordinal]

            val width = textureFrame.width
            val height = textureFrame.height

            val drawOrder = textureFrame.drawOrder
            val format = textureFrame.colourFormat

            val bytes = textureFrame.bytes
            val unpacked = bytes.unpackBit4()

            val framePixmap = drawPixmap(width, height, drawOrder, unpacked, format, palette)
            sheet.drawPixmap(framePixmap, 0, y)

            y += height
        }

        loadedPixmaps[parameter.id.value shl 16 or parameter.coloration.ordinal] = sheet
    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        fileHandle: FileHandle?,
        parameter: OverworldMonsterTextureParameter?
    ): OverworldMonsterTexture {
        val pixmap = loadedPixmaps[parameter!!.id.value shl 16 or parameter.coloration.ordinal]
            ?: throw IllegalArgumentException()
        loadedPixmaps.remove(parameter.id.value shl 16 or parameter.coloration.ordinal)
        return OverworldMonsterTexture(Texture(pixmap))
    }

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: OverworldMonsterTextureParameter?
    ): Array<AssetDescriptor<*>> {
        val descriptors = Array<AssetDescriptor<*>>()

        descriptors.add(AssetDescriptor(MonsterConfig::class.java.simpleName, MonsterConfig::class.java))
        descriptors.add(
            AssetDescriptor(
                "archive-${ArchiveType.OVERWORLD_ENTITIES}", Archive::class.java, ArchiveParameter(
                    ArchiveType.OVERWORLD_ENTITIES
                )
            )
        )

        return descriptors
    }
}