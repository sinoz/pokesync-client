package io.pokesync.core.login

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField

/**
 * The email input field in which the user may enter the e-mail address of their account.
 * @author Sino
 */
class EmailField(skin: Skin) : TextField("", skin, "credentials-input-box")