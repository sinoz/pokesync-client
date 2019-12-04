package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.core.assets.config.AnimSeqConfig
import java.nio.file.Path

/**
 * An [AssetLoaderParameters] for animation sequences.
 * @author Sino
 */
class AnimationConfigParameter(val directory: Path) : AssetLoaderParameters<AnimSeqConfig>()