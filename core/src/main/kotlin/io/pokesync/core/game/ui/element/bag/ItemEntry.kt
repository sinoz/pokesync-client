package io.pokesync.core.game.ui.element.bag

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/**
 * A display box presenting an item with the given [BagItemProfile].
 * @author Sino
 */
class ItemEntry(profile: BagItemProfile, skin: Skin) : Button(skin, "entry-box") {
    private val image = Image()

    private val tooltip = TextTooltip("Quantity: 1", skin, "entry-tooltip")

    init {
        image.drawable = TextureRegionDrawable(profile.texture.underlying)

        val imageWidth = profile.texture.underlying.width.toFloat()
        val imageHeight = profile.texture.underlying.height.toFloat()

        image.width = imageWidth
        image.height = imageHeight

        val centerX = prefWidth / 2 - image.width / 2
        val centerY = prefHeight / 2 - image.height / 2

        val adjustedX = centerX - 4F
        val adjustedY = centerY + 4F

        image.setPosition(adjustedX, adjustedY)

        tooltip.setInstant(true)

        addActor(image)
        addListener(tooltip)
    }

    fun setQuantity(amount: Int) {
        tooltip.container.actor.setText("Quantity: $amount")
    }
}