package io.pokesync.rom

import arrow.core.Try
import arrow.effects.IO
import com.google.protobuf.ByteString
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/**
 * A file loaded from disk.
 * @author Sino
 */
data class DiskFile(val bytes: ByteString) {
    companion object {
        /**
         * A predicate that checks if the given file [Path] ends with the NDS extension.
         */
        fun ofNdsExtension(path: Path): Boolean =
            path.fileName.toString().endsWith(".nds")

        /**
         * A predicate that checks if the given file [Path] ends with the GBA extension.
         */
        fun ofGbaExtension(path: Path): Boolean =
            path.fileName.toString().endsWith(".gba")

        /**
         * Searches for a file on disk that matches the specified predicate.
         */
        fun findBy(directory: Path, p: (Path) -> Boolean): IO<DiskFile> =
            IO.async { _, cb ->
                Files.walkFileTree(directory, object : SimpleFileVisitor<Path>() {
                    override fun visitFile(filePath: Path, fileAttr: BasicFileAttributes): FileVisitResult {
                        if (p(filePath)) {
                            cb(Try { unsafeLoad(filePath) }.toEither())
                            return FileVisitResult.TERMINATE
                        }

                        return FileVisitResult.CONTINUE
                    }
                })
            }

        /**
         * Loads a [DiskFile] at the specified [Path].
         */
        fun load(path: Path): IO<DiskFile> =
            IO { unsafeLoad(path) }

        /**
         * Unsafely loads a [DiskFile] at the specified [Path]. May throw an exception.
         */
        private fun unsafeLoad(path: Path): DiskFile =
            DiskFile(ByteString.copyFrom(Files.readAllBytes(path)))
    }
}