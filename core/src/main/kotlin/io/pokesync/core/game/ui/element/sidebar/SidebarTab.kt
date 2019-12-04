package io.pokesync.core.game.ui.element.sidebar

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.style.get

/**
 * A sidebar tab.
 * @author Sino
 */
class SidebarTab(val type: Type, skin: Skin, pressed: (Type) -> Unit) :
    Button(skin, "${getStyleName(type.ordinal)}-idle") {
    /**
     * A type of sidebar tab.
     */
    enum class Type(val styleSuffix: String) {
        ITEM_BAG("bag-icon"),

        SKILLS ("skills-icon"),

        QUESTS ("quest-icon"),

        ACHIEVEMENTS ("achievements-icon"),

        DEX ("dex-icon"),

        CHATBOX ("chat-icon"),

        SETTINGS ("settings-icon")
    }

    /**
     * A flag that indicates whether the tab was clicked on or not.
     */
    private var clicked = false

    /**
     * The icon to attach to this tab.
     */
    private val image = Image(skin, "${type.styleSuffix}-idle")

    init {
        addListener(object : InputListener() {
            override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                style = skin["${getStyleName(type.ordinal)}-hovered"]

                width = prefWidth
                height = prefHeight

                image.drawable = skin["${type.styleSuffix}-hovered"]

                image.width = image.prefWidth
                image.height = image.prefHeight
            }

            override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                if (!clicked) {
                    style = skin["${getStyleName(type.ordinal)}-idle"]

                    width = prefWidth
                    height = prefHeight

                    image.drawable = skin["${type.styleSuffix}-idle"]

                    image.width = image.prefWidth
                    image.height = image.prefHeight
                }

                clicked = false
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                clicked = true

                pressed(type)

                return true
            }
        })

        add(image).padBottom(8F)
    }

    /**
     * Presents this sidebar tab.
     */
    fun show() {
        isVisible = true
    }

    /**
     * Hides this sidebar tab.
     */
    fun hide() {
        isVisible = false
    }

    companion object {
        fun getStyleName(sidebarTabId: Int): String =
            if (sidebarTabId == 0) "top-sidebar" else "sidebar"
    }
}