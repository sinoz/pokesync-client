package io.pokesync.lib.gdx

import com.badlogic.gdx.scenes.scene2d.ui.ButtonedScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.style.SkinDsl
import ktx.style.addStyle
import ktx.style.defaultStyle
import ktx.style.get

/**
 * @param name name of the style as it will appear in the [Skin] instance.
 * @param extend optional name of an _existing_ style of the same type. Its values will be copied and used as base for
 *    this style.
 * @param init will be applied to the style instance. Inlined.
 * @return a new instance of [ScrollPaneStyle] added to the [Skin] with the selected name.
 */
inline fun Skin.buttonedScrollPane(name: String = defaultStyle,
                                   extend: String? = null,
                                   init: (@SkinDsl ButtonedScrollPane.SimpleScrollPaneStyle).() -> Unit = {}) =
    addStyle(name, if (extend == null) ButtonedScrollPane.SimpleScrollPaneStyle() else ButtonedScrollPane.SimpleScrollPaneStyle(get(extend)), init)