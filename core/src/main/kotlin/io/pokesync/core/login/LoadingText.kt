package io.pokesync.core.login

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * The loading text to present while loading assets.
 * @author Sino
 */
class LoadingText(skin: Skin) : Label("Please wait...", skin, "loading-text")