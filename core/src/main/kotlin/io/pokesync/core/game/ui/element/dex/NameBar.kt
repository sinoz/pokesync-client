package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * The name display.
 * @author Sino
 */
class NameBar(skin: Skin) : Table(skin) {
    /**
     * The name label of the monster.
     */
    private val name = Label("", skin, "name")

    /**
     * The title of the monster e.g 'RENEGADE MONSTER'.
     */
    private val title = Label("", skin, "title")

    init {
        background = skin["name-display"]

        add(name).expandX().fillX().left().padLeft(5F).padBottom(4F).row()
        add(title).expandX().fillX().left().padLeft(5F).padBottom(10F).row()
    }

    /**
     * Sets the name of the monster.
     */
    fun setMonsterName(text: String) {
        name.setText(text)
    }

    /**
     * Sets the title of the monster.
     */
    fun setTitle(text: String) {
        title.setText(text)
    }
}