package io.pokesync.core.game.ui.element.bag

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import ktx.style.get

/**
 * A pocket in the item bag containing a list of items.
 * @author Sino
 */
class Pocket(val type: PocketType, skin: Skin, pressed: (PocketType) -> Unit) :
    Button(skin, "${type.styleName}-pocket-idle") {
    private val items = mutableListOf<Item>()

    private var isActive = false

    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                pressed(type)
            }
        })

        setAsInactive()
    }

    fun add(item: Item) {
        items.add(item)
    }

    fun getItems(): List<Item> =
        items

    fun setAsActive() {
        style = skin["${type.styleName}-pocket-selected"]
        isActive = true
    }

    fun setAsInactive() {
        style = skin["${type.styleName}-pocket-idle"]
        isActive = false
    }
}