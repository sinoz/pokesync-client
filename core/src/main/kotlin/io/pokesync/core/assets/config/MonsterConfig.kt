package io.pokesync.core.assets.config

import arrow.effects.IO
import io.netty.buffer.Unpooled
import io.pokesync.core.assets.exception.UnexpectedFileStampException
import io.pokesync.lib.bytes.readCString
import io.pokesync.lib.bytes.readString
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Contains information of each monster.
 * @author Sino
 */
data class MonsterConfig(val entries: List<Entry>) {
    data class Entry(
        val id: Int,
        val nameLabelId: Int,

        val height: Int,
        val weight: Int,

        val primaryType: String,
        val secondaryType: String,

        val overworldFileId: Int,

        val maleFrontSpriteGraphicFileId: Int,
        val maleBackSpriteGraphicFileId: Int,

        val femaleFrontSpriteGraphicFileId: Int,
        val femaleBackSpriteGraphicFileId: Int,

        val regularPaletteId: Int,
        val shinyPaletteId: Int
    )

    companion object {
        /**
         * Loads a binary config file at the specified [Path].
         */
        fun load(path: Path): IO<MonsterConfig> = IO {
            val bytes = Files.readAllBytes(path)
            val buffer = Unpooled.wrappedBuffer(bytes)

            val dataFileStamp = buffer.readString(4)
            if (dataFileStamp != ConfigType.MONSTERS.fileStamp) {
                throw UnexpectedFileStampException(dataFileStamp)
            }

            val headerLength = buffer.readUnsignedByte()
            if (headerLength < 0 || headerLength > buffer.readableBytes()) {
                throw IOException()
            }

            buffer.readUnsignedByte() // export version

            val payloadLength = buffer.readInt()
            val monsterCount = buffer.readUnsignedShort()

            if (payloadLength < 0 || payloadLength > buffer.readableBytes()) {
                throw IOException()
            }

            val monsterEntries = mutableListOf<Entry>()
            for (i in 0 until monsterCount) {
                val id = buffer.readUnsignedShort()
                val nameLabelId = buffer.readUnsignedShort()

                val height = buffer.readUnsignedShort()
                val weight = buffer.readUnsignedShort()

                val primaryType = buffer.readCString()
                val secondaryType = buffer.readCString()

                val overworldFileId = buffer.readUnsignedShort()

                val maleFrontSpriteGraphicFileId = buffer.readUnsignedShort()
                val maleBackSpriteGraphicFileId = buffer.readUnsignedShort()

                val femaleFrontSpriteGraphicFileId = buffer.readUnsignedShort()
                val femaleBackSpriteGraphicFileId = buffer.readUnsignedShort()

                val regularPaletteId = buffer.readUnsignedShort()
                val shinyPaletteId = buffer.readUnsignedShort()

                monsterEntries.add(
                    Entry(
                        id, nameLabelId,
                        height, weight,
                        primaryType, secondaryType,

                        overworldFileId,
                        maleFrontSpriteGraphicFileId,
                        maleBackSpriteGraphicFileId,

                        femaleFrontSpriteGraphicFileId,
                        femaleBackSpriteGraphicFileId,

                        regularPaletteId,
                        shinyPaletteId
                    )
                )
            }

            MonsterConfig(monsterEntries)
        }
    }
}