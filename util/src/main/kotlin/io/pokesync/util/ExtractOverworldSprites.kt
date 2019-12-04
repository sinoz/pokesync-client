package io.pokesync.util

import arrow.effects.IO
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import io.pokesync.rom.graphics.drawPixmap
import io.pokesync.lib.bytes.unpackBit4
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import io.pokesync.rom.DiskFile.Companion.ofNdsExtension
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
            val archive = DiskFile.findBy(Paths.get("resources/roms/images/"), ::ofNdsExtension)
                .bind { IO { Image.create(it) } }
                .bind { IO { it.fileSystem.lookupFile(210)!! } } // /a/0/8/1
                .bind { IO { Archive.read(it) } }
                .unsafeRunSync()

            extractOverworldPlayers(archive)
//            extractOverworldNpcs(archive)
//            extractOverworldMonsters(archive)
        }

        private fun cake(tex: Texture, sheet: Pixmap, offset: Int) {
            for (frameId in tex.frames.indices) {
                val frame = tex.frames[frameId]
                val palette = tex.palettes[0]

                val width = frame.width
                val height = frame.height

                val format = frame.colourFormat
                val drawOrder = frame.drawOrder

                val bytes = frame.bytes
                val unpacked = bytes.unpackBit4()

                val framePixmap = drawPixmap(width, height, drawOrder, unpacked, format, palette)
                sheet.drawPixmap(framePixmap, 0, offset + (frameId * height))
            }
        }

        private fun dumpMalePlayer(archive: Archive) {
            val tex1 = archive.fileSystem.lookupFile(69)!!.readAsBTX0()
            val tex2 = archive.fileSystem.lookupFile(71)!!.readAsBTX0()
            val tex3 = archive.fileSystem.lookupFile(73)!!.readAsBTX0()
            val tex4 = archive.fileSystem.lookupFile(79)!!.readAsBTX0()

            val characterHeight = tex1.frames[0].height

            val sheetLength = tex1.frames.size + tex2.frames.size + tex3.frames.size + tex4.frames.size
            val sheet = Pixmap(tex1.frames[0].width, sheetLength * tex1.frames[0].height, Pixmap.Format.RGBA8888)

            cake(tex1, sheet, offset = 0)
            cake(tex2, sheet, offset = tex1.frames.size * characterHeight)
            cake(tex3, sheet, offset = (tex1.frames.size + tex2.frames.size) * characterHeight)
            cake(tex4, sheet, offset = (tex1.frames.size + tex2.frames.size + tex3.frames.size) * characterHeight)

            PixmapIO.writePNG(Gdx.files.local("resources/world/players/0.png"), sheet)
        }

        private fun dumpFemalePlayer(archive: Archive) {
            val tex1 = archive.fileSystem.lookupFile(70)!!.readAsBTX0()
            val tex2 = archive.fileSystem.lookupFile(72)!!.readAsBTX0()
            val tex3 = archive.fileSystem.lookupFile(74)!!.readAsBTX0()
            val tex4 = archive.fileSystem.lookupFile(80)!!.readAsBTX0()

            val characterHeight = tex1.frames[0].height

            val sheetLength = tex1.frames.size + tex2.frames.size + tex3.frames.size + tex4.frames.size
            val sheet = Pixmap(tex1.frames[0].width, sheetLength * tex1.frames[0].height, Pixmap.Format.RGBA8888)

            cake(tex1, sheet, offset = 0)
            cake(tex2, sheet, offset = tex1.frames.size * characterHeight)
            cake(tex3, sheet, offset = (tex1.frames.size + tex2.frames.size) * characterHeight)
            cake(tex4, sheet, offset = (tex1.frames.size + tex2.frames.size + tex3.frames.size) * characterHeight)

            PixmapIO.writePNG(Gdx.files.local("resources/world/players/1.png"), sheet)
        }

        private fun extractOverworldPlayers(archive: Archive) {
            fun dump(fileIds: List<Int>, gender: Int) {
                var x = 0

                for (fileId in fileIds) {
                    val file = archive.fileSystem.lookupFile(fileId)!!
                    val tex = file.readAsBTX0()

                    for (frameId in tex.frames.indices) {
                        val frame = tex.frames[frameId]
                        val palette = tex.palettes[0]

                        val width = frame.width
                        val height = frame.height

                        val format = frame.colourFormat
                        val drawOrder = frame.drawOrder

                        val bytes = frame.bytes
                        val unpacked = bytes.unpackBit4()

                        val framePixmap = drawPixmap(width, height, drawOrder, unpacked, format, palette)
                        PixmapIO.writePNG(Gdx.files.local("resources/world/players/$gender/$x.png"), framePixmap)

                        x++
                    }
                }
            }

            dump(listOf(69, 71, 73, 79), 0)
            dump(listOf(70, 72, 74, 80), 1)
        }

        private fun extractOverworldNpcs(archive: Archive) {
            for (npcId in 0 until 266) {
                if (npcId in 69..90 && npcId != 83 && npcId != 84) {
                    // 69 to 90 are player sprites except for 83 and 84
                    continue
                }

                val file = archive.fileSystem.lookupFile(npcId)!!
                val tex = file.readAsBTX0()

                val sheet = Pixmap(tex.frames[0].width, tex.frames.size * tex.frames[0].height, Pixmap.Format.RGBA8888)
                for (frameId in tex.frames.indices) {
                    val frame = tex.frames[frameId]
                    val palette = tex.palettes[0]

                    val width = frame.width
                    val height = frame.height

                    val format = frame.colourFormat
                    val drawOrder = frame.drawOrder

                    val bytes = frame.bytes
                    val unpacked = bytes.unpackBit4()

                    val framePixmap = drawPixmap(width, height, drawOrder, unpacked, format, palette)
                    sheet.drawPixmap(framePixmap, 0, frameId * height)
                }

                PixmapIO.writePNG(Gdx.files.local("resources/world/npcs/$npcId.png"), sheet)
            }
        }

        private fun extractOverworldMonsters(archive: Archive) {
            for (paletteId in 0..1) {
                for (monsterId in 0 until 565) {
                    val file = archive.fileSystem.lookupFile(297 + monsterId)!!
                    val tex = file.readAsBTX0()

                    val sheet = Pixmap(tex.frames[0].width, tex.frames.size * tex.frames[0].height, Pixmap.Format.RGBA8888)
                    for (frameId in tex.frames.indices) {
                        val frame = tex.frames[frameId]
                        val palette = tex.palettes[paletteId]

                        val width = frame.width
                        val height = frame.height

                        val format = frame.colourFormat
                        val drawOrder = frame.drawOrder

                        val bytes = frame.bytes
                        val unpacked = bytes.unpackBit4()

                        val framePixmap = drawPixmap(width, height, drawOrder, unpacked, format, palette)
                        sheet.drawPixmap(framePixmap, 0, frameId * height)
                    }

                    PixmapIO.writePNG(Gdx.files.local("resources/world/monsters/$paletteId/$monsterId.png"), sheet)
                }
            }
        }

        override fun dispose() {

        }

    })
}