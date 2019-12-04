package io.pokesync.core.game.ui.element.hud

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.ui.provider.PlayerSpriteProvider
import ktx.style.get

/**
 * The status hud bar.
 * @author Sino
 */
class HudBar(skin: Skin, val playerSpriteProvider: PlayerSpriteProvider) : Button(skin, "hud") {
    private val pokeDollarIcon = Image(skin, "inactive-pokedollar-icon")
    private val pokeDollarLabel = Label("Loading...", skin, "pokedollars")

    private val donatorPointsIcon = Image(skin, "inactive-donation-pts-icon")
    private val donatorPointsLabel = Label("Loading...", skin, "donator-points")

    private val timeIcon = Image(skin, "inactive-nighttime-icon")
    private val timeLabel = Label("????", skin, "time")

    private var hovered = false
    private var isNight = true

    init {
        addListener(object : InputListener() {
            override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                pokeDollarIcon.drawable = skin["active-pokedollar-icon"]
                pokeDollarLabel.color = Color.GREEN

                donatorPointsIcon.drawable = skin["active-donation-pts-icon"]
                donatorPointsLabel.color = Color.ORANGE

                if (isNight) {
                    timeIcon.drawable = skin["active-nighttime-icon"]
                } else {
                    timeIcon.drawable = skin["active-daytime-icon"]
                }

                hovered = true
            }

            override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                pokeDollarIcon.drawable = skin["inactive-pokedollar-icon"]
                pokeDollarLabel.color = Color.WHITE

                donatorPointsIcon.drawable = skin["inactive-donation-pts-icon"]
                donatorPointsLabel.color = Color.WHITE

                if (isNight) {
                    timeIcon.drawable = skin["inactive-nighttime-icon"]
                } else {
                    timeIcon.drawable = skin["inactive-daytime-icon"]
                }

                hovered = false
            }
        })

        add(createCurrencyGroup()).grow().left().padLeft(5F).padBottom(13F)
        add(createTimeGroup()).grow().padRight(42F)
    }

    /**
     * Fetches the player's character sprite by the given [Gender] and draws it
     * onto the hud.
     */
    fun assignCharacter(gender: Gender) {
        // TODO
    }

    /**
     * Updates the amount of pokedollar coins the user has.
     */
    fun setPokeDollars(value: Int) {
        pokeDollarLabel.setText("$value")
    }

    /**
     * Updates the amount of donator points the user has.
     */
    fun setDonatorPoints(value: Int) {
        donatorPointsLabel.setText("$value")
    }

    /**
     * Updates the server time.
     */
    fun setTime(hour: Int, minutes: Int) {
        if (hour < 12) {
            if (hovered) {
                timeIcon.drawable = skin["active-nighttime-icon"]
            } else {
                timeIcon.drawable = skin["inactive-nighttime-icon"]
            }

            isNight = true
        } else {
            if (hovered) {
                timeIcon.drawable = skin["active-daytime-icon"]
            } else {
                timeIcon.drawable = skin["inactive-daytime-icon"]
            }

            isNight = false
        }

        timeLabel.setText("${String.format("%02d", hour)}:${String.format("%02d", minutes)}")
    }

    /**
     * Constructs a [Table] for the currency display.
     */
    private fun createCurrencyGroup(): Table {
        val currencyGroup = Table()

        currencyGroup.add(pokeDollarIcon)
        currencyGroup.add(pokeDollarLabel)
            .padLeft(3F)
            .growX()
            .width(52F)
            .left()
            .row()

        currencyGroup.add(donatorPointsIcon)
        currencyGroup.add(donatorPointsLabel)
            .padLeft(3F)
            .growX()
            .width(52F)
            .left()
            .row()

        currencyGroup.pack()

        return currencyGroup
    }

    /**
     * Constructs a [Table] for the time display.
     */
    private fun createTimeGroup(): Table {
        val timeGroup = Table()

        timeGroup.add(timeIcon)
        timeGroup.add(timeLabel).padLeft(3F)

        return timeGroup
    }
}