package io.pokesync.lib.gdx

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager

/**
 * Urgently enqueues an asset for loading and waits for it to finish.
 */
inline fun <reified T> AssetManager.getOrLoad(fileName: String, parameter: AssetLoaderParameters<T>? = null): T {
    val descriptor = AssetDescriptor(fileName, T::class.java, parameter)

    return if (isLoaded(fileName)) {
        get(descriptor)
    } else {
        load(descriptor)
        finishLoadingAsset(descriptor)
    }
}