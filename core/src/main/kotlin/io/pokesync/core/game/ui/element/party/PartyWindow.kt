package io.pokesync.core.game.ui.element.party

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import io.pokesync.core.assets.BattleTextureAspect
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.core.game.model.StatusCondition
import io.pokesync.core.game.ui.provider.MonsterProfileProvider
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.lib.gdx.verticalSheetSlice

/**
 * The pokemon party window.
 * @author Sino
 */
class PartyWindow(skin: Skin, val monsterProfileProvider: MonsterProfileProvider) : Window("", skin, "party") {
    private val monsterSlots = arrayOfNulls<MonsterSlot?>(PARTY_SIZE)

    private val dragAndDrop = DragAndDrop()

    init {
        isMovable = true
        isResizable = false

        width = prefWidth
        height = prefHeight

        for (slotId in 0 until PARTY_SIZE) {
            val slot = MonsterSlot(skin)

            val summaryButtonHeight = slot.summaryButton.prefHeight
            val slotY = 250F - ((summaryButtonHeight + 5F) * slotId)

            slot.summaryButton.add(slot.monsterImage)
            slot.summaryButton.setPosition(9F, slotY)

            slot.statusConditionImage.setPosition(11F, slotY)
            slot.genderImage.setPosition(slot.summaryButton.prefWidth, slotY)

            addActor(slot.summaryButton)
            addActor(slot.statusConditionImage)
            addActor(slot.genderImage)

            slot.statusConditionImage.toFront()
            slot.genderImage.toFront()

            monsterSlots[slotId] = slot
        }
    }

    /**
     * Adds the given [Actor] as a valid drop target for dragging party monsters.
     */
    fun addDropTarget(actor: Actor, dropped: (Int) -> Unit) {
        dragAndDrop.addTarget(object : DragAndDrop.Target(actor) {
            override fun drop(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload,
                x: Float,
                y: Float,
                pointer: Int
            ) {
                dropped(payload.`object` as Int)
            }

            override fun drag(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ): Boolean {
                return true
            }
        })
    }

    /**
     * Turns each and every slot within this party into a target for a drag-and-drop.
     */
    fun makeAllSlotsDragAndDropTargets(f: (Int, Int) -> Unit) {
        for (curSlotId in monsterSlots.indices) {
            val slot = monsterSlots[curSlotId]!!

            dragAndDrop.addSource(object : DragAndDrop.Source(slot.summaryButton) {
                override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                    val payload = DragAndDrop.Payload()

                    payload.`object` = curSlotId

                    payload.validDragActor = Image(slot.monsterImage.drawable)
                    payload.dragActor = Image(slot.monsterImage.drawable)

                    return payload
                }
            })

            for (otherSlotId in monsterSlots.indices) {
                val otherSlot = monsterSlots[curSlotId]!!
                if (otherSlotId == curSlotId) {
                    continue
                }

                dragAndDrop.addTarget(object : DragAndDrop.Target(otherSlot.summaryButton) {
                    override fun drop(
                        source: DragAndDrop.Source?,
                        payload: DragAndDrop.Payload,
                        x: Float,
                        y: Float,
                        pointer: Int
                    ) {
                        f(curSlotId, payload.`object` as Int)
                    }

                    override fun drag(
                        source: DragAndDrop.Source?,
                        payload: DragAndDrop.Payload?,
                        x: Float,
                        y: Float,
                        pointer: Int
                    ): Boolean {
                        return true
                    }
                })
            }
        }
    }

    /**
     * Attaches the [TextureRegion] of a monster to the specified slot.
     */
    fun attachMonsterToSlot(
        slot: Int,
        id: ModelId,
        gender: Gender?,
        coloration: MonsterColoration,
        statusCondition: StatusCondition?
    ) {
        require(!(slot < 0 || slot >= monsterSlots.size))

        val profile = monsterProfileProvider.provide(id, gender, coloration, BattleTextureAspect.FRONT)

        val overworldTexture = profile.overworldTexture.underlying
        val overworldFrame = overworldTexture.verticalSheetSlice()[2]

        monsterSlots[slot]!!.attachMonsterSprite(gender, statusCondition, overworldFrame)
    }

    /**
     * Detaches a monster from the given slot, if any present.
     */
    fun removeMonsterFromSlot(slot: Int) {
        require(!(slot < 0 || slot >= monsterSlots.size))

        monsterSlots[slot]!!.detachMonsterSprite()
    }

    companion object {
        /**
         * The amount of monsters to allow on each belt.
         */
        const val PARTY_SIZE = 6
    }
}