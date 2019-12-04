package io.pokesync.util

import arrow.effects.IO
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.Image
import io.pokesync.rom.nitro.readAsTextBank
import java.nio.file.Paths

fun main() {
    val archive = DiskFile.findBy(Paths.get("resources/roms/images/"), DiskFile.Companion::ofNdsExtension)
        .bind { IO { Image.create(it) } }
        .bind { IO { it.fileSystem.lookupFile(156)!! } } // /a/0/2/7
        .bind { IO { Archive.read(it) } }
        .unsafeRunSync()

    val file = archive.fileSystem.lookupFile(803)!!
    val textLabelBank = file.readAsTextBank()

    println(textLabelBank)
}