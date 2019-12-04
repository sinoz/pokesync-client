package io.pokesync.util

import arrow.effects.IO
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.Image
import io.pokesync.rom.nitro.readAsMapMatrix
import java.nio.file.Paths

fun main() {
    val archive = DiskFile.findBy(Paths.get("resources/roms/images/"), DiskFile.Companion::ofNdsExtension)
        .bind { IO { Image.create(it) } }
        .bind { IO { it.fileSystem.lookupFile(0xAA)!! } } // /a/0/4/1
        .bind { IO { Archive.read(it) } }
        .unsafeRunSync()

    for (fileId in 0 until archive.fileSystem.fileCount()) {
        val file = archive.fileSystem.lookupFile(fileId)!!
        val matrix = file.readAsMapMatrix()
    }
}