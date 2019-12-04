package io.pokesync.util

import arrow.effects.IO
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.graphics.PixmapIO
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.lib.bytes.unpackBit16
import io.pokesync.rom.crypto.decipherDiamondAndPearl
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
            val image = DiskFile.findBy(Paths.get("resources/roms/images/"), DiskFile.Companion::ofNdsExtension)
                .bind { IO { Image.create(it) } }

            val backImageArchive = image
                .bind { IO { it.fileSystem.lookupFile(0x87)!! } } // /a/0/0/6
                .bind { IO { Archive.read(it) } }
                .unsafeRunSync()

            val frontImageArchive = image
                .bind { IO { it.fileSystem.lookupFile(0xBB)!! } } // /a/0/5/8
                .bind { IO { Archive.read(it) } }
                .unsafeRunSync()

            extractTrainerBackSprites(backImageArchive)
            extractTrainerFrontSprites(frontImageArchive)
        }

        fun extractTrainerFrontSprites(archive: Archive) {
            for ((trainerId, trainerFileId) in (4..639 step 5).withIndex()) {
                val graphicFile = archive.fileSystem.lookupFile(trainerFileId)!!
                val paletteFile = archive.fileSystem.lookupFile(trainerFileId - 3)!!

                val pokeFrame = graphicFile.readAsNCGR()
                val pokePalette = paletteFile.readAsNCLR()[0]

                val width = pokeFrame.width
                val height = pokeFrame.height

                val format = pokeFrame.colourFormat
                val drawOrder = pokeFrame.drawOrder

                val rawBytes = pokeFrame.bytes
                val unpackedAndDeciphered = unpackBit16(rawBytes.decipherDiamondAndPearl())

                val pixmap = drawPixmap(width, height, drawOrder, unpackedAndDeciphered, format, pokePalette)

                PixmapIO.writePNG(Gdx.files.local("resources/trainers/front/$trainerId.png"), pixmap)
            }
        }

        fun extractTrainerBackSprites(archive: Archive) {
            for ((trainerId, trainerFileId) in (4..84 step 5).withIndex()) {
                val graphicFile = archive.fileSystem.lookupFile(trainerFileId)!!
                val paletteFile = archive.fileSystem.lookupFile(trainerFileId - 3)!!

                val pokeFrame = graphicFile.readAsNCGR()
                val pokePalette = paletteFile.readAsNCLR()[0]

                val width = pokeFrame.width
                val height = pokeFrame.height

                val format = pokeFrame.colourFormat
                val drawOrder = pokeFrame.drawOrder

                val rawBytes = pokeFrame.bytes
                val unpackedAndDeciphered = unpackBit16(rawBytes.decipherDiamondAndPearl())

                val pixmap = drawPixmap(width, height, drawOrder, unpackedAndDeciphered, format, pokePalette)

                PixmapIO.writePNG(Gdx.files.local("resources/trainers/back/$trainerId.png"), pixmap)
            }
        }

        override fun dispose() {

        }

    })
}