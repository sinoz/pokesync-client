package io.pokesync.rom.nitro

import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap

/**
 * The Nitro file system.
 * @author Sino
 */
class FileSystem(private val files: Int2ObjectSortedMap<File>) {
    /**
     * Looks up a [File] in this file system, by its id.
     */
    fun lookupFile(id: Int): File? =
        files[id]

    /**
     * Returns the amount of files that exist within this file system.
     */
    fun fileCount(): Int =
        files.size
}