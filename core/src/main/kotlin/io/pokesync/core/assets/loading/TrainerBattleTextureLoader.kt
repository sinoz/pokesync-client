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
import io.pokesync.core.assets.TrainerBattleTexture
import io.pokesync.lib.bytes.unpackBit16
import io.pokesync.rom.crypto.decipherDiamondAndPearl
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.readAsNCGR
import io.pokesync.rom.nitro.readAsNCLR
import java.util.concurrent.ConcurrentHashMap

/**
 * An [AsynchronousAssetLoader] that loads [TrainerBattleTexture]s asynchronously into memory.
 * @author Sino
 */
class TrainerBattleTextureLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<TrainerBattleTexture, TrainerBattleTextureParameter>(resolver) {
    private val loadedPixmaps = ConcurrentHashMap<Int, Pixmap>()

    override fun loadAsync(
        manager: AssetManager,
        fileName: String?,
        fileHandle: FileHandle?,
        parameter: TrainerBattleTextureParameter
    ) {
        val archiveDescriptor = AssetDescriptor(
            "archive-${ArchiveType.TRAINERS}", Archive::class.java, ArchiveParameter(
                ArchiveType.TRAINERS
            )
        )
        val archive = manager.get(archiveDescriptor)!!

        val graphicFileId = FILE_OFFSET + (parameter.id.value * 5)
        val graphicFile = archive.fileSystem.lookupFile(graphicFileId)!!

        val paletteFileId = graphicFileId - 3
        val paletteFile = archive.fileSystem.lookupFile(paletteFileId)!!

        val frame = graphicFile.readAsNCGR()
        val palette = paletteFile.readAsNCLR()[0]

        val width = frame.width
        val height = frame.height

        val format = frame.colourFormat
        val drawOrder = frame.drawOrder

        val rawBytes = frame.bytes
        val unpackedAndDeciphered = unpackBit16(rawBytes.decipherDiamondAndPearl())

        val sheet = drawPixmap(width, height, drawOrder, unpackedAndDeciphered, format, palette)
        val textureId = graphicFileId shl 16 or paletteFileId

        loadedPixmaps[textureId] = sheet
    }

    override fun loadSync(
        manager: AssetManager,
        fileName: String?,
        fileHandle: FileHandle?,
        parameter: TrainerBattleTextureParameter?
    ): TrainerBattleTexture {
        val graphicFileId = FILE_OFFSET + (parameter!!.id.value * 5)
        val paletteFileId = graphicFileId - 3

        val textureId = graphicFileId shl 16 or paletteFileId

        val pixmap = loadedPixmaps[textureId] ?: throw IllegalArgumentException()
        loadedPixmaps.remove(textureId)

        return TrainerBattleTexture(Texture(pixmap))
    }

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: TrainerBattleTextureParameter?
    ): Array<AssetDescriptor<*>> {
        val descriptors = Array<AssetDescriptor<*>>()
        descriptors.add(
            AssetDescriptor(
                "archive-${ArchiveType.TRAINERS}", Archive::class.java, ArchiveParameter(
                    ArchiveType.TRAINERS
                )
            )
        )
        return descriptors
    }

    companion object {
        const val FILE_OFFSET = 4
    }
}