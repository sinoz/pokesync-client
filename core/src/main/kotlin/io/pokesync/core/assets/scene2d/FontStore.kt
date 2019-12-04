package io.pokesync.core.assets.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ObjectMap

/**
 * A store of [BitmapFont]s.
 * @author Sino
 */
class FontStore : Disposable {
    /**
     * A type of font.
     */
    enum class Type(val inSkinIdentifier: String) {
        WHITE_ROBOTO_MONO_PT10 ("roboto-mono-pt10-white"),

        WHITE_ROBOTO_MONO_PT11 ("roboto-mono-pt11-white"),

        BLACK_ROBOTO_MONO_PT11 ("roboto-mono-pt11-black"),

        GOOD_MAN ("good-man"),

        DONKEY_BERRY ("donkey-berry"),
    }

    private val fonts = mutableMapOf<Type, BitmapFont>()

    fun put(type: Type, font: BitmapFont) {
        fonts[type] = font
    }

    fun get(type: Type): BitmapFont? =
        fonts[type]

    fun remove(type: Type) {
        fonts.remove(type)
    }

    fun getSkinView(): ObjectMap<String, Any> {
        val fonts = ObjectMap<String, Any>()
        for (font in Type.values()) {
            fonts.put(font.inSkinIdentifier, this.fonts[font])
        }

        return fonts
    }

    override fun dispose() {
        for (font in fonts.values) {
            font.dispose()
        }

        fonts.clear()
    }

    companion object {
        /**
         * Creates a new [FontStore].
         */
        fun create(): FontStore {
            val store = FontStore()

            val robotoMonoWhite10 = createRobotoMonoFont(Color.WHITE, 10)
            val robotoMonoWhite11 = createRobotoMonoFont(Color.WHITE, 11)
            val robotoMonoBlack11 = createRobotoMonoFont(Color.BLACK, 11)

            store.put(Type.WHITE_ROBOTO_MONO_PT10, robotoMonoWhite10)
            store.put(Type.WHITE_ROBOTO_MONO_PT11, robotoMonoWhite11)
            store.put(Type.BLACK_ROBOTO_MONO_PT11, robotoMonoBlack11)
            store.put(Type.GOOD_MAN, createGoodManFont())
            store.put(Type.DONKEY_BERRY, createDonkeyBerryFont())

            return store
        }

        /**
         * Constructs a [BitmapFont] for the 'Roboto Mono' font with the specified size and default [Color].
         */
        private fun createRobotoMonoFont(defaultColor: Color, size: Int): BitmapFont {
            val generator = FreeTypeFontGenerator(Gdx.files.internal("resources/ui/fonts/roboto-mono-regular.ttf"))
            val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()

            parameter.color = defaultColor
            parameter.size = size

            return generator.generateFont(parameter)
        }

        /**
         * Constructs a [BitmapFont] for the 'Donkey-Berry' font.
         */
        private fun createDonkeyBerryFont() =
            BitmapFont(Gdx.files.internal("resources/ui/fonts/donkey-berry.fnt"))

        /**
         * Constructs a [BitmapFont] for the 'Good-Man' font.
         */
        private fun createGoodManFont() =
            BitmapFont(Gdx.files.internal("resources/ui/fonts/good-man.fnt"))
    }
}