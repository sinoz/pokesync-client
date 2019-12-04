package io.pokesync.lib.gdx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ktx.async.KtxAsync

/**
 * An alias function for [KtxAsync.launch] to operate
 * behavior on the Gdx's rendering thread.
 */
fun onRenderingThread(f: suspend CoroutineScope.() -> Unit) {
    KtxAsync.launch {
        f()
    }
}