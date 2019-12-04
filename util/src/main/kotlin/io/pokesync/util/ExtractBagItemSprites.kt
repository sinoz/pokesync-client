package io.pokesync.util

import arrow.effects.IO
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.graphics.PixmapIO
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import io.pokesync.rom.nitro.*
import java.nio.file.Paths
import kotlin.math.max

// TODO fix

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
                .bind { IO { it.fileSystem.lookupFile(147)!! } } // /a/0/1/8
                .bind { IO { Archive.read(it) } }
                .unsafeRunSync()

            fun getFiles(offset: Int, p: (File) -> Boolean): List<File> {
                var incr = 0
                val list = mutableListOf<File>()
                while (true) {
                    val nextFile = frontImageArchive.fileSystem.lookupFile(offset + incr)
                    if (nextFile != null) {
                        if (p(nextFile)) {
                            list.add(nextFile)
                        } else {
                            break
                        }
                    }

                    incr++
                }

                return list
            }

            var itemId = 0
            var itemFileId = 2
            while (itemFileId <= 795) {
                val graphicFiles = getFiles(offset = itemFileId) { it.metadata.extension == "RGCN" }
                val paletteFiles = getFiles(offset = itemFileId + max(1, graphicFiles.size)) { it.metadata.extension == "RLCN" }
                if (graphicFiles.size > 1 && paletteFiles.size > 1) {
                    throw Exception()
                }

                when {
                    graphicFiles.size > 1 -> {
                        // multiple graphic files, one palette (different items that share the same palette, used in some categorizations)
                        for (graphicFile in graphicFiles) {
                            dump(itemId, graphicFile, paletteFiles[0])
                            itemId++
                        }
                    }

                    paletteFiles.size > 1 -> {
                        // one graphic file, multiple palettes (potions of different types: different colour but same sprite)
                        for (paletteFile in paletteFiles) {
                            dump(itemId, graphicFiles[0], paletteFile)
                            itemId++
                        }
                    }

                    else -> {
                        // one graphic file, one palette. the default use case
                        dump(itemId, graphicFiles[0], paletteFiles[0])
                        itemId++
                    }
                }

                itemFileId += graphicFiles.size + paletteFiles.size
            }
        }

        fun dump(id: Int, graphicFile: File, paletteFile: File) {
            val pokeFrame = graphicFile.readAsNCGR()
            val pokePalette = paletteFile.readAsNCLR()[0]

            val width = pokeFrame.width
            val height = pokeFrame.height

            val drawOrder = pokeFrame.drawOrder

            val format = pokeFrame.colourFormat
            val rawBytes = pokeFrame.bytes

            val pixmap = drawPixmap(width, height, drawOrder, rawBytes, format, pokePalette)

            PixmapIO.writePNG(Gdx.files.local("resources/items/$id.png"), pixmap)
        }

        override fun dispose() {

        }

    })
}