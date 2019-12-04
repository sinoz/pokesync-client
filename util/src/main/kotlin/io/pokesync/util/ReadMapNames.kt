package io.pokesync.util

import arrow.effects.IO
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import io.pokesync.rom.nitro.Image
import io.pokesync.rom.nitro.readAsMapLabelBank
import java.nio.file.Paths

fun main() {
    val image = DiskFile.findBy(Paths.get("resources/roms/images/"), DiskFile.Companion::ofNdsExtension)
        .bind { IO { Image.create(it) } }
        .unsafeRunSync()

    val file = image.fileSystem.lookupFile(0x1E5)!!
    val labels = file.readAsMapLabelBank()
}