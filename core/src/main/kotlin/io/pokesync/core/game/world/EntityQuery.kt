package io.pokesync.core.game.world

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.pokesync.core.game.world.component.Kind
import io.pokesync.core.game.world.component.Transformable
import ktx.ashley.get
import kotlin.math.floor

/**
 * Linearly searches for a monster [Entity] at the specified world coordinates.
 */
fun Engine.getMonsterAt(worldX: Int, worldZ: Int): Entity? {
    val entityCount = entities.size()
    for (entityIndex in 0 until entityCount) {
        val entity = entities[entityIndex]!!
        val kind = entity.get<Kind>()
        if (kind == null || kind != Kind.MONSTER) {
            continue
        }

        val transformable = entity.get<Transformable>()!!

        val npcX = floor(transformable.position.x).toInt()
        val npcZ = floor(transformable.position.y).toInt()
        if (npcX == worldX && npcZ == worldZ) {
            return entity
        }
    }

    return null
}

/**
 * Linearly searches for an NPC [Entity] at the specified world coordinates.
 */
fun Engine.getNpcAt(worldX: Int, worldZ: Int): Entity? {
    val entityCount = entities.size()
    for (entityIndex in 0 until entityCount) {
        val entity = entities[entityIndex]!!
        val kind = entity.get<Kind>()
        if (kind == null || (kind != Kind.NPC && kind != Kind.MONSTER)) {
            continue
        }

        val transformable = entity.get<Transformable>()!!

        val npcX = floor(transformable.position.x).toInt()
        val npcZ = floor(transformable.position.y).toInt()
        if (npcX == worldX && npcZ == worldZ) {
            return entity
        }
    }

    return null
}

/**
 * Collects a list of [Entity] players that are standing at the specified
 * world coordinates.
 */
fun Engine.getPlayersAt(worldX: Int, worldZ: Int): List<Entity> {
    val playerList = mutableListOf<Entity>()

    val entityCount = entities.size()
    for (entityIndex in 0 until entityCount) {
        val entity = entities[entityIndex]!!
        val kind = entity.get<Kind>()
        if (kind == null || kind != Kind.PLAYER) {
            continue
        }

        val transformable = entity.get<Transformable>()!!

        val playerX = floor(transformable.position.x).toInt()
        val playerZ = floor(transformable.position.y).toInt()
        if (playerX == worldX && playerZ == worldZ) {
            playerList.add(entity)
        }
    }

    return playerList
}