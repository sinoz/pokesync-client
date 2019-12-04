package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.core.assets.config.ItemConfig
import java.nio.file.Path

/**
 * TODO
 * @author Sino
 */
class ItemConfigParameter(val directory: Path) : AssetLoaderParameters<ItemConfig>()