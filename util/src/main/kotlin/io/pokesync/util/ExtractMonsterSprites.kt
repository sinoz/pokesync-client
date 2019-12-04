package io.pokesync.util

import arrow.effects.IO
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.graphics.PixmapIO
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.lib.bytes.unpackBit16
import io.pokesync.rom.crypto.decipherPlatinum
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import io.pokesync.rom.nitro.*
import java.nio.file.Paths

fun main() {
    LwjglApplication(object : ApplicationListener {
        override fun render() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun pause() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun resume() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun resize(width: Int, height: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun create() {
            val frontImageArchive = DiskFile.findBy(Paths.get("resources/roms/images/"), DiskFile.Companion::ofNdsExtension)
                .bind { IO { Image.create(it) } }
                .bind { IO { it.fileSystem.lookupFile(133)!! } } // /a/0/0/4
                .bind { IO { Archive.read(it) } }
                .unsafeRunSync()

            var fileId = 0
            for ((monsterId, fileIdOffset) in (0..2959 step 6).withIndex()) {
                for (type in 0 until 4) {
                    val graphicFile = frontImageArchive.fileSystem.lookupFile(fileIdOffset + type)!!
                    if (graphicFile.metadata.extension != "RGCN") {
                        fileId++
                        continue
                    }

                    val paletteFile = frontImageArchive.fileSystem.lookupFile(fileIdOffset + 4)!!

                    val pokeFrame = graphicFile.readAsNCGR()
                    val pokePalette = paletteFile.readAsNCLR()[0]

                    val width = pokeFrame.width
                    val height = pokeFrame.height

                    val format = pokeFrame.colourFormat
                    val drawOrder = pokeFrame.drawOrder

                    val rawBytes = pokeFrame.bytes
                    val unpackedAndDeciphered = unpackBit16(rawBytes.decipherPlatinum())

                    val pixmap = drawPixmap(width, height, drawOrder, unpackedAndDeciphered, format, pokePalette)

                    PixmapIO.writePNG(Gdx.files.local("resources/monsters/${fileIdOffset + type}.png"), pixmap)
                    fileId++
                }
            }

//            for ((monsterId, fileIdOffset) in (0..2959 step 6).withIndex()) {
//                for (paletteId in 0..1) {
//                    for (type in 0..4) {
//                        val graphicFile = frontImageArchive.fileSystem.lookupFile(fileIdOffset + type)!!
//                        if (graphicFile.metadata.extension != "RGCN") {
//                            continue
//                        }
//
//                        val paletteFile = frontImageArchive.fileSystem.lookupFile(fileIdOffset + 4 + paletteId)!!
//
//                        val pokeFrame = graphicFile.readAsNCGR()
//                        val pokePalette = paletteFile.readAsNCLR()[0]
//
//                        val width = pokeFrame.width
//                        val height = pokeFrame.height
//
//                        val drawOrder = pokeFrame.drawOrder
//
//                        val rawBytes = pokeFrame.bytes
//                        val unpackedAndDeciphered = unpackBit16(rawBytes.decipherPlatinum())
//
//                        val pixmap = drawPixmap(width, height, drawOrder, unpackedAndDeciphered, pokePalette)
//
//                        val typeFolder = if (type == 0 || type == 1) "back" else "front"
//                        val genderFolder = if (type == 0 || type == 2) "female" else "male"
//
//                        PixmapIO.writePNG(Gdx.files.local("resources/monsters/$typeFolder/$genderFolder/$paletteId/$monsterId.png"), pixmap)
//                    }
//                }
//            }
        }

        override fun dispose() {

        }

    })
}