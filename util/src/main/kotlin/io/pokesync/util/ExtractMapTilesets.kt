package io.pokesync.util

import arrow.effects.IO
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import io.pokesync.lib.bytes.unpackBit2
import io.pokesync.lib.bytes.unpackBit4
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import io.pokesync.rom.graphics.ColourFormat
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.Image
import io.pokesync.rom.nitro.readAsBTX0
import java.nio.file.Paths

fun main() {
    LwjglApplication(object : ApplicationAdapter() {
        override fun create() {
            val archive = DiskFile.findBy(Paths.get("resources/roms/images/"), DiskFile.Companion::ofNdsExtension)
                .bind { IO { Image.create(it) } }
                .bind { IO { it.fileSystem.lookupFile(0xAD)!! } } // /a/0/4/4
                .bind { IO { Archive.read(it) } }
                .unsafeRunSync()

            for (fileId in 0 until 106) {
                val textureFile = archive.fileSystem.lookupFile(fileId)!!
                val texture = textureFile.readAsBTX0()

                val sheet = Pixmap(256, 2048, Pixmap.Format.RGBA8888)
                var y = 0

                for (frameId in texture.frames.indices) {
                    val frame = texture.frames[frameId]
                    val frameLabel = texture.frameLabels[frameId]

                    var paletteId = texture.paletteLabels.indexOfFirst { it == frameLabel || it.replace("_pl", "") == frameLabel }
                    if (paletteId == -1) {
                        paletteId = frameId
                    }

                    println(paletteId)
                    val palette = texture.palettes[paletteId]

                    val width = frame.width
                    val height = frame.height

                    val drawOrder = frame.drawOrder
                    val format = frame.colourFormat

                    val bytes = frame.bytes
                    val unpacked = when (frame.colourFormat) {
                        ColourFormat.BIT2 -> bytes.unpackBit2()
                        ColourFormat.BIT4 -> bytes.unpackBit4()
                        else -> bytes
                    }

                    val framePixmap = drawPixmap(width, height, drawOrder, unpacked, format, palette)
                    sheet.drawPixmap(framePixmap, 0, y)

                    y += height
                }

                PixmapIO.writePNG(Gdx.files.local("resources/world/tilesets/$fileId.png"), sheet)
                println("Extracted tileset $fileId")
            }
        }
    })
}