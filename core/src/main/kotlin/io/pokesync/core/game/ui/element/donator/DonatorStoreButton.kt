package io.pokesync.core.game.ui.element.donator

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.style.get

/**
 * A button displayed in the top right corner of the screen, which is to open up
 * the donator store interface.
 * @author Sino
 */
class DonatorStoreButton(skin: Skin, pressed: () -> Unit) : Button(skin, "donator-store-idle") {
    /**
     * A flag that indicates whether the tab was clicked on or not.
     */
    private var clicked = false

    init {
        addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                style = skin["donator-store-hovered"]

                width = prefWidth
                height = prefHeight

                // if the user hasn't clicked on the button, we can safely roll
                // out the scroll-like button to present it as hovered over
                if (!clicked) {
                    setY(getY() - 9)
                }
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                // user hasn't clicked on it, restore the button's position
                if (!clicked) {
                    style = skin["donator-store-idle"]

                    width = prefWidth
                    height = prefHeight

                    setY(getY() + 9)
                }

                clicked = false
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                clicked = true

                pressed()

                return true
            }
        })
    }
}