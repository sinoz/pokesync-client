package io.pokesync.core.assets.scene2d

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import io.pokesync.lib.gdx.getOrLoad

/**
 * A store of [Skin]s.
 * @author Sino
 */
class SkinStore : Disposable {
    /**
     * A type of skin.
     */
    enum class Type {
        LOGIN_BG,

        LOGIN_ELEMENTS,

        ITEM_BAG,

        GAME,

        MONSTER_DEX,

        PAUSE_BG
    }

    private val skins = mutableMapOf<Type, Skin>()

    fun put(type: Type, skin: Skin) {
        skins[type] = skin
    }

    fun get(type: Type): Skin? =
        skins[type]

    fun remove(type: Type) {
        skins.remove(type)
    }

    override fun dispose() {
        for (skin in skins.values) {
            skin.dispose()
        }

        skins.clear()
    }

    companion object {
        fun create(fontStore: FontStore, assets: AssetManager): SkinStore {
            val store = SkinStore()
            val fontsForSkins = fontStore.getSkinView()

            store.put(Type.LOGIN_BG, assets.getOrLoad("resources/ui/login_bg.json", SkinLoader.SkinParameter(fontsForSkins)))
            store.put(Type.PAUSE_BG, assets.getOrLoad("resources/ui/pause_bg.json", SkinLoader.SkinParameter(fontsForSkins)))
            store.put(Type.LOGIN_ELEMENTS, assets.getOrLoad("resources/ui/login.json", SkinLoader.SkinParameter(fontsForSkins)))
            store.put(Type.MONSTER_DEX, assets.getOrLoad("resources/ui/dex.json", SkinLoader.SkinParameter(fontsForSkins)))
            store.put(Type.ITEM_BAG, assets.getOrLoad("resources/ui/bag.json", SkinLoader.SkinParameter(fontsForSkins)))
            store.put(Type.GAME, assets.getOrLoad("resources/ui/game.json", SkinLoader.SkinParameter(fontsForSkins)))

            return store
        }
    }
}