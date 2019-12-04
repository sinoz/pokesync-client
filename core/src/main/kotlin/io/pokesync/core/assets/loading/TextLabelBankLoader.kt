package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import io.pokesync.rom.nitro.ArchiveType
import io.pokesync.core.assets.TextBankType
import io.pokesync.core.assets.TextLabelBank
import io.pokesync.rom.nitro.Archive
import io.pokesync.rom.nitro.readAsTextBank
import java.util.concurrent.ConcurrentHashMap

/**
 * An [AsynchronousAssetLoader] that loads [TextLabelBank]s asynchronously into memory.
 * @author Sino
 */
class TextLabelBankLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<TextLabelBank, TextLabelParameter>(resolver) {
    private val loadedBanks = ConcurrentHashMap<TextBankType, TextLabelBank>()

    override fun loadAsync(manager: AssetManager, fileName: String?, file: FileHandle?, parameter: TextLabelParameter) {
        val archiveDescriptor = AssetDescriptor(
            "archive-${ArchiveType.TEXT_LABELS}", Archive::class.java, ArchiveParameter(
                ArchiveType.TEXT_LABELS
            )
        )
        val archive = manager.get(archiveDescriptor)!!

        val bankFile = archive.fileSystem.lookupFile(parameter.bankType.nitroFileId)!!
        val textBank = TextLabelBank(bankFile.readAsTextBank())

        loadedBanks[parameter.bankType] = textBank
    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: TextLabelParameter?
    ): TextLabelBank {
        val bank = loadedBanks[parameter!!.bankType] ?: throw IllegalArgumentException()
        loadedBanks.remove(parameter.bankType)
        return bank
    }

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: TextLabelParameter?
    ): Array<AssetDescriptor<*>> {
        val descriptors = Array<AssetDescriptor<*>>()
        descriptors.add(
            AssetDescriptor(
                "archive-${ArchiveType.TEXT_LABELS}", Archive::class.java, ArchiveParameter(
                    ArchiveType.TEXT_LABELS
                )
            )
        )
        return descriptors
    }
}