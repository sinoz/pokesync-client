package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.rom.nitro.Image
import java.nio.file.Path

/**
 * TODO
 * @author Sino
 */
class NitroImageParameter(val directory: Path) : AssetLoaderParameters<Image>()