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
import io.pokesync.core.assets.OverworldPlayerTexture
import io.pokesync.core.game.model.Gender
import io.pokesync.lib.bytes.unpackBit4
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.readAsBTX0
import java.util.concurrent.ConcurrentHashMap

/**
 * An [AsynchronousAssetLoader] that loads [OverworldPlayerTexture]s asynchronously into memory.
 * @author Sino
 */
class OverworldPlayerTextureLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<OverworldPlayerTexture, OverworldPlayerTextureParameter>(resolver) {
    private val loadedPixmaps = ConcurrentHashMap<Gender, Pixmap>()

    override fun loadAsync(
        manager: AssetManager,
        fileName: String?,
        fileHandle: FileHandle?,
        parameter: OverworldPlayerTextureParameter
    ) {
        val archiveDescriptor = AssetDescriptor(
            "archive-${ArchiveType.OVERWORLD_ENTITIES}", Archive::class.java, ArchiveParameter(
                ArchiveType.OVERWORLD_ENTITIES
            )
        )
        val archive = manager.get(archiveDescriptor)!!

        val fileIds = FILE_IDS[parameter.gender] ?: error("No file ids associated with gender ${parameter.gender}")

        var sheetWidth = 0
        var sheetHeight = 0

        val textureFiles = mutableListOf<io.pokesync.rom.nitro.Texture>()
        for (fileId in fileIds) {
            val file = archive.fileSystem.lookupFile(fileId)!!
            val textureFile = file.readAsBTX0()

            sheetWidth = textureFile.frames[0].width
            sheetHeight += textureFile.frames[0].height * textureFile.frames.size

            textureFiles.add(textureFile)
        }

        val sheet = Pixmap(sheetWidth, sheetHeight, Pixmap.Format.RGBA8888)
        var y = 0

        for (textureFile in textureFiles) {
            for (textureFrame in textureFile.frames) {
                val palette = textureFile.palettes[0]

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
        }

        loadedPixmaps[parameter.gender] = sheet
    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        fileHandle: FileHandle?,
        parameter: OverworldPlayerTextureParameter?
    ): OverworldPlayerTexture {
        val pixmap = loadedPixmaps[parameter!!.gender] ?: throw IllegalArgumentException()
        loadedPixmaps.remove(parameter.gender)
        return OverworldPlayerTexture(Texture(pixmap))
    }

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: OverworldPlayerTextureParameter?
    ): Array<AssetDescriptor<*>> {
        val descriptors = Array<AssetDescriptor<*>>()
        descriptors.add(
            AssetDescriptor(
                "archive-${ArchiveType.OVERWORLD_ENTITIES}", Archive::class.java, ArchiveParameter(
                    ArchiveType.OVERWORLD_ENTITIES
                )
            )
        )
        return descriptors
    }

    companion object {
        val FILE_IDS = mapOf(
            Pair(Gender.MALE, listOf(69, 71, 73, 79)),
            Pair(Gender.FEMALE, listOf(70, 72, 74, 80))
        )
    }
}