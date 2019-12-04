package io.pokesync.core.game.ui.element.hud

import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.ray3k.tenpatch.TenPatchDrawable
import ktx.style.get

/**
 * A text dialogue.
 * @author Sino
 */
class Dialogue(skin: Skin) : Table(skin) {
    /**
     * The position of the dialogue on the screen.
     */
    val screenPoint = Vector3()

    /**
     * The glyph layout.
     */
    val glyphLayout = GlyphLayout()

    /**
     * The amount of time that has passed since the last advancement.
     */
    private var timePassed = 0F

    /**
     * The speed at which characters are advancing in time.
     */
    private var speed = 0.1F

    /**
     * The index in the [targetText] sequence.
     */
    private var textIndex = 0

    /**
     * The text progress.
     */
    private var currentText = StringBuilder()

    /**
     * The text for this dialogue to result into.
     */
    private var targetText = ""

    /**
     * The list of dialogue lines as [Label]s.
     */
    private val dialogue = Label("", skin, "dialogue-text")

//    /**
//     * The tooth to give the user an idea that the dialogue can be continued.
//     */
//    private val tooth = Image(skin, "dialogue-tooth")

    init {
        background = skin.get("popup-10", TenPatchDrawable::class.java)
        dialogue.setWrap(true)

        add(dialogue)
            .expand()
            .fill(0.95F, 1F)
            .left()
            .top()
            .padLeft(6F)
            .padBottom(6F)

//        add(tooth)
//            .bottom()
//            .right()
    }

    /**
     * Presents this dialogue and its elements.
     */
    fun show() {
        isVisible = true
    }

    /**
     * Hides this dialogue and its elements.
     */
    fun hide() {
        isVisible = false
    }

    /**
     * Checks if it is time to advance to a next character in the sequence,
     * by checking if the amount of time that has passed since the last
     * advancement, has surpassed the [speed].
     */
    fun timeToAdvance(): Boolean =
        timePassed >= speed

    /**
     * Adds to the amount of time that has passed since the last
     * character advancement.
     */
    fun addTimePassed(amount: Float) {
        timePassed += amount
    }

    /**
     * Resets the amount of time that has passed since the last
     * character advancement. Used when a character advancement
     * has occurred.
     */
    fun clearTimePassing() {
        timePassed = 0F
    }

    /**
     * Prepares the given [String] to show in the dialogue.
     */
    fun prepareTextToDisplay(s: String) {
        targetText = s
        glyphLayout.setText(dialogue.style.font, s)

        width = prefWidth
        height = prefHeight

        currentText.clear()
        textIndex = 0
        dialogue.setText("")
    }

    /**
     * Advances a character to add to the dialogue.
     */
    fun advanceCharacter() {
        val nextCharacter = targetText[textIndex]
        val updatedLine = dialogue.text.toString() + nextCharacter

        dialogue.setText(updatedLine)

        textIndex++
    }

    /**
     * Increases the speed at which the dialogue text is appearing.
     */
    fun increaseSpeed() {
        speed = SPED_UP_DIALOGUE
    }

    /**
     * Restores the speed at which the dialogue text is appearing
     * back to its normal state.
     */
    fun restoreSpeed() {
        speed = DIALOGUE_SPEED
    }

    /**
     * Checks if the [textIndex] exceeds the length of the [targetText],
     * which indicates that the dialogue has finished printing each character
     * to the dialogue box.
     */
    fun finishedPrinting(): Boolean =
        textIndex >= targetText.length

    companion object {
        /**
         * The speed at which characters show up.
         */
        const val DIALOGUE_SPEED = 0.05F

        /**
         * The acceleration to add to the [DIALOGUE_SPEED] factor when the
         * user has held in the 'continue' keypad.
         */
        const val SPED_UP_DIALOGUE = 0.005F
    }
}