package io.pokesync.core.game.ui.element.dex

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.style.get

/**
 * Draws a monster sprite.
 * @author Sino
 */
class MonsterView(skin: Skin) : Table(skin) {
    private val image = Image()

    init {
        background = skin["monster-display"]

        add(image)
            .center()
            .padRight(4F)
            .padBottom(4F)

        pack()
    }

    fun setMonsterSprite(region: TextureRegion) {
        image.drawable = TextureRegionDrawable(region)
    }
}