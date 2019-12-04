package io.pokesync.core.game.ui.element.party

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.StatusCondition
import ktx.style.get

/**
 * A slot that carries a monster.
 * @author Sino
 */
class MonsterSlot(val skin: Skin) {
    // TODO find a way to represent status condition and gender as Scene2D Image's and not Button's!

    /**
     * The summary button that opens up the monster summary.
     */
    val summaryButton = Button(skin, "party-slot")

    /**
     * The [Image] of a monster to draw on top of this slot.
     */
    val monsterImage = Image()

    /**
     * The [Image] of the status condition a monster may be suffering from.
     */
    val statusConditionImage = Button(skin, "status-frozen")

    /**
     * The [Image] of the monster's gender.
     */
    val genderImage = Button(skin, "gender-man")

    init {
        summaryButton.isVisible = false
        statusConditionImage.isVisible = false
        genderImage.isVisible = false
    }

    /**
     * Attaches the [TextureRegion] of a monster to this [MonsterSlot].
     */
    fun attachMonsterSprite(gender: Gender?, statusCondition: StatusCondition?, textureRegion: TextureRegion) {
        summaryButton.isVisible = true

        setGenderStyle(gender)
        setStatusConditionStyle(statusCondition)
        setMonsterDrawable(textureRegion)
    }

    /**
     * Updates the drawable behind the [monsterImage].
     */
    private fun setMonsterDrawable(textureRegion: TextureRegion) = with(monsterImage) {
        drawable = TextureRegionDrawable(textureRegion)
        isVisible = true
    }

    /**
     * Updates the style behind the [genderImage].
     */
    private fun setGenderStyle(gender: Gender?) = with(genderImage) {
        isVisible = gender != null
        if (gender != null) {
            style = skin[GENDERS[gender] ?: error("Gender $gender not associated with any style")]

            // refresh the size of the sprite
            width = prefWidth
            height = prefHeight
        }
    }

    /**
     * Updates the style behind the [statusConditionImage].
     */
    private fun setStatusConditionStyle(statusCondition: StatusCondition?) = with(statusConditionImage) {
        isVisible = statusCondition != null
        if (statusCondition != null) {
            style = skin[STATUS_CONDITIONS[statusCondition]
                ?: error("StatusCondition $statusCondition not associated with any style")]

            // refresh the size of the sprite
            width = prefWidth
            height = prefHeight
        }
    }

    /**
     * Detaches a monster sprite from this slot, if present.
     */
    fun detachMonsterSprite() {
        monsterImage.drawable = null

        summaryButton.isVisible = false
        monsterImage.isVisible = false
        statusConditionImage.isVisible = false
        genderImage.isVisible = false
    }

    companion object {
        /**
         * A mapping of all [Gender]s to style names.
         */
        val GENDERS = mapOf(
            Pair(Gender.MALE, "gender-man"),
            Pair(Gender.FEMALE, "gender-woman")
        )

        /**
         * A mapping of all [StatusCondition]s to style names.
         */
        val STATUS_CONDITIONS = mapOf(
            Pair(StatusCondition.ASLEEP, "status-asleep"),
            Pair(StatusCondition.BURNT, "status-burnt"),
            Pair(StatusCondition.PARALYZED, "status-paralyzed"),
            Pair(StatusCondition.FROZEN, "status-frozen"),
            Pair(StatusCondition.POISONED, "status-poisoned")
        )
    }
}