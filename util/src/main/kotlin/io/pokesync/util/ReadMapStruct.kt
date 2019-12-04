package io.pokesync.util

import arrow.effects.IO
import io.pokesync.lib.bytes.asReadOnlyByteBuf
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.Image
import io.pokesync.rom.nitro.readAsMapSruct
import java.nio.file.Paths

fun main() {
    val archive = DiskFile.findBy(Paths.get("resources/roms/images/"), DiskFile.Companion::ofNdsExtension)
        .bind { IO { Image.create(it) } }
        .bind { IO { it.fileSystem.lookupFile(0xC2)!! } } // /a/0/6/5
        .bind { IO { Archive.read(it) } }
        .unsafeRunSync()

    val file = archive.fileSystem.lookupFile(0)!!
    val mapStruct = file.readAsMapSruct()
}