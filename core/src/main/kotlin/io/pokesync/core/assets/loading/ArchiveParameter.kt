package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.rom.nitro.ArchiveType
import io.pokesync.rom.nitro.Archive

/**
 * TODO
 * @author Sino
 */
class ArchiveParameter(val type: ArchiveType) : AssetLoaderParameters<Archive>()