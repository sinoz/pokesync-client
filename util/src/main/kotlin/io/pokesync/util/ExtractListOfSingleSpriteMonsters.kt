package io.pokesync.util

import arrow.effects.IO
import io.pokesync.lib.effect.bind
import io.pokesync.rom.DiskFile
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.Image
import java.nio.file.Paths

fun main() {
    val frontImageArchive = DiskFile.findBy(Paths.get("resources/roms/images/"), DiskFile.Companion::ofNdsExtension)
        .bind { IO { Image.create(it) } }
        .bind { IO { it.fileSystem.lookupFile(133)!! } } // /a/0/0/4
        .bind { IO { Archive.read(it) } }
        .unsafeRunSync()

    val maleOnly = mutableListOf<Int>()
    val femaleOnly = mutableListOf<Int>()

    cake@
    for ((monsterId, fileIdOffset) in (0..2959 step 6).withIndex()) {
        val femaleBackSprite = frontImageArchive.fileSystem.lookupFile(fileIdOffset)!!
        val maleBackSprite = frontImageArchive.fileSystem.lookupFile(fileIdOffset + 1)!!

        val femaleFrontSprite = frontImageArchive.fileSystem.lookupFile(fileIdOffset + 2)!!
        val maleFrontSprite = frontImageArchive.fileSystem.lookupFile(fileIdOffset + 3)!!
        if (maleBackSprite.metadata.extension != "RGCN" && femaleBackSprite.metadata.extension == "RGCN") {
            femaleOnly.add(monsterId)
            continue@cake
        }

        if (maleBackSprite.metadata.extension == "RGCN" && femaleBackSprite.metadata.extension != "RGCN") {
            maleOnly.add(monsterId)
            continue@cake
        }
    }

    println("Male only: $maleOnly")
    println("Female only: $femaleOnly")
}