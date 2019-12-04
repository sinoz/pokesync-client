package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip
import io.pokesync.core.game.world.component.ModelId
import ktx.style.get

/**
 * A dex entry.
 * @author Sino
 */
class DexEntry(
    id: ModelId,
    skin: Skin,
    val profile: MonsterProfile,
    monsterTexture: TextureRegion,
    pressed: (ModelId) -> Unit
) : Button(skin, "uncaught-dex-entry") {
    private val image = Image(monsterTexture)

    private val tooltip = TextTooltip(profile.name, skin, "entry-tooltip")

    init {
        addActor(image)

        // TODO get rid of the transparency from the image
        if (monsterTexture.regionWidth == 32) {
            image.x = 8F
            image.y = 16F
        } else if (monsterTexture.regionWidth == 64) {
            image.x = -6F
            image.y = 12F
        }

        markAsRegistered()
        pack()

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                pressed(id)
                return true
            }
        })

        tooltip.setInstant(true)
    }

    /**
     * Marks the monster as unregistered.
     */
    fun markAsUnregistered() {
        style = skin["uncaught-dex-entry"]
        image.color = Color.BLACK

        removeListener(tooltip)
    }

    /**
     * Marks the monster as being registered.
     */
    fun markAsRegistered() {
        style = skin["caught-dex-entry"]
        image.color = Color(1F, 1F, 1F, 1F)

        addListener(tooltip)
    }
}