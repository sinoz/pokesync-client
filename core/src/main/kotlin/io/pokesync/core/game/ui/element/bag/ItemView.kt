package io.pokesync.core.game.ui.element.bag

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.pokesync.core.game.ui.provider.BagItemProfileProvider
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.lib.gdx.onRenderingThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A view of items in a [Pocket].
 * @author Sino
 */
class ItemView(val pockets: Map<PocketType, Pocket>, skin: Skin, val bagItemProfileProvider: BagItemProfileProvider) :
    Table(skin) {
    /**
     * Displays the items in the specified [PocketType].
     */
    fun switchToPocket(type: PocketType) {
        clearChildren()

        for (pocket in pockets.values) {
            pocket.setAsInactive()
        }

        val targetPocket = pockets[type]
        targetPocket!!.setAsActive()

        val itemList = targetPocket.getItems()

        val colsPerRow = 7

        var rowId = 0
        var colId = 0

        for (item in itemList) {
            val profile = bagItemProfileProvider.provide(ModelId(item.id))

            val itemEntry = ItemEntry(profile, skin)
            val itemEntryCell = add(itemEntry)

            colId++
            if (colId >= colsPerRow) {
                itemEntryCell.row()

                colId = 0
                rowId++
            }
        }
    }
}