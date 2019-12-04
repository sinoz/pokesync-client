package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array
import io.pokesync.core.assets.*
import io.pokesync.core.assets.config.MonsterConfig
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.lib.bytes.unpackBit16
import io.pokesync.rom.crypto.decipherPlatinum
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.ArchiveType
import io.pokesync.rom.nitro.readAsNCGR
import io.pokesync.rom.nitro.readAsNCLR
import java.util.concurrent.ConcurrentHashMap

/**
 * An [AsynchronousAssetLoader] that loads [MonsterBattleTexture]s asynchronously into memory.
 * @author Sino
 */
class MonsterBattleTextureLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<MonsterBattleTexture, MonsterBattleTextureParameter>(resolver) {
    private val loadedPixmaps = ConcurrentHashMap<Int, Pixmap>()

    /**
     * TODO
     */
    private fun selectGraphicFileId(
        configEntry: MonsterConfig.Entry,
        gender: Gender?,
        aspect: BattleTextureAspect
    ): Int {
        // in case a monster is gender/sex-restricted, we have to adjust the requested gender
        var adjustedGender = gender
        if (configEntry.maleFrontSpriteGraphicFileId == 65535 || configEntry.maleBackSpriteGraphicFileId == 65535) {
            adjustedGender = Gender.FEMALE
        }

        if (configEntry.femaleFrontSpriteGraphicFileId == 65535 || configEntry.femaleBackSpriteGraphicFileId == 65535) {
            adjustedGender = Gender.MALE
        }

        var graphicFileId = 0
        if ((adjustedGender == null || adjustedGender == Gender.MALE)) {
            graphicFileId = if (aspect == BattleTextureAspect.FRONT) {
                configEntry.maleFrontSpriteGraphicFileId
            } else {
                configEntry.maleBackSpriteGraphicFileId
            }
        } else if (adjustedGender == Gender.FEMALE) {
            graphicFileId = if (aspect == BattleTextureAspect.FRONT) {
                configEntry.femaleFrontSpriteGraphicFileId
            } else {
                configEntry.femaleBackSpriteGraphicFileId
            }
        }

        return graphicFileId
    }

    /**
     * TODO
     */
    private fun selectPaletteFileId(configEntry: MonsterConfig.Entry, coloration: MonsterColoration): Int {
        return when (coloration) {
            MonsterColoration.REGULAR -> configEntry.regularPaletteId
            MonsterColoration.SHINY -> configEntry.shinyPaletteId
        }
    }

    override fun loadAsync(
        manager: AssetManager,
        fileName: String?,
        fileHandle: FileHandle?,
        parameter: MonsterBattleTextureParameter
    ) {
        val config = manager.get(MonsterConfig::class.java.simpleName, MonsterConfig::class.java)!!
        val archive = manager.get("archive-${ArchiveType.MONSTERS}", Archive::class.java)!!

        val configEntry = config.entries[parameter.id.value]

        val graphicFileId = selectGraphicFileId(configEntry, parameter.gender, parameter.aspect)
        val paletteFileId = selectPaletteFileId(configEntry, parameter.coloration)

        val graphicFile = archive.fileSystem.lookupFile(graphicFileId)!!
        val paletteFile = archive.fileSystem.lookupFile(paletteFileId)!!

        val frame = graphicFile.readAsNCGR()
        val palette = paletteFile.readAsNCLR()[0]

        val width = frame.width
        val height = frame.height

        val format = frame.colourFormat
        val drawOrder = frame.drawOrder

        val rawBytes = frame.bytes
        val unpackedAndDeciphered = unpackBit16(rawBytes.decipherPlatinum())

        val sheet = drawPixmap(width, height, drawOrder, unpackedAndDeciphered, format, palette)

        loadedPixmaps[graphicFileId shl 16 or paletteFileId] = sheet
    }

    override fun loadSync(
        manager: AssetManager,
        fileName: String?,
        fileHandle: FileHandle?,
        parameter: MonsterBattleTextureParameter?
    ): MonsterBattleTexture {
        val config = manager.get(MonsterConfig::class.java.simpleName, MonsterConfig::class.java)!!
        val configEntry = config.entries[parameter!!.id.value]

        val graphicFileId = selectGraphicFileId(configEntry, parameter.gender, parameter.aspect)
        val paletteFileId = selectPaletteFileId(configEntry, parameter.coloration)

        val textureId = graphicFileId shl 16 or paletteFileId

        val pixmap = loadedPixmaps[textureId] ?: throw IllegalArgumentException()
        loadedPixmaps.remove(textureId)

        return MonsterBattleTexture(Texture(pixmap))
    }

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: MonsterBattleTextureParameter?
    ): Array<AssetDescriptor<*>> {
        val descriptors = Array<AssetDescriptor<*>>()

        descriptors.add(AssetDescriptor(MonsterConfig::class.java.simpleName, MonsterConfig::class.java))
        descriptors.add(
            AssetDescriptor(
                "archive-${ArchiveType.MONSTERS}", Archive::class.java, ArchiveParameter(
                    ArchiveType.MONSTERS
                )
            )
        )

        return descriptors
    }
}