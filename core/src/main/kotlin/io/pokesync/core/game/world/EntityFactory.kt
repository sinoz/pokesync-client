package io.pokesync.core.game.world

import com.badlogic.ashley.core.Entity
import io.pokesync.core.account.UserGroup
import io.pokesync.core.assets.texture.ShadowTexture
import io.pokesync.core.game.GameAssets
import io.pokesync.core.game.model.*
import io.pokesync.core.game.world.component.*
import io.pokesync.lib.gdx.verticalSheetSlice
import kotlin.math.max

/**
 * Produces [Entity]s.
 * @author Sino
 */
class EntityFactory(val worldGrid: WorldGrid, val assets: GameAssets) {
    /**
     * Produces an avatar which is controlled by the user of this game client
     * application instance.
     *
     * An avatar is simply a player entity with a [CameraFocused] and an [InputData]
     * included in its component bag.
     */
    fun createAvatar(
        pid: PID,
        gender: Gender,
        position: MapPosition,
        direction: Direction,
        displayName: DisplayName,
        userGroup: UserGroup
    ): Entity {
        return createPlayer(pid, gender, position, direction, displayName, userGroup)
            .add(InputData())
            .add(CameraFocused())
    }

    /**
     * Produces a player entity.
     */
    fun createPlayer(
        pid: PID,
        gender: Gender,
        position: MapPosition,
        direction: Direction,
        displayName: DisplayName,
        userGroup: UserGroup
    ): Entity {
        val tileMap = worldGrid.lookupMap(position.mapX, position.mapZ)!!

        val mapOffsetX = tileMap.properties["ox"] as Int
        val mapOffsetY = tileMap.properties["oy"] as Int

        val worldX = mapOffsetX + max(0, position.localX)
        val worldZ = mapOffsetY + max(0, position.localZ)

        val playerTexture = assets.obtainOverworldPlayerTexture(gender).underlying.verticalSheetSlice()
        val baseSprite = BaseSprite.createPlayerSprite(worldX, worldZ, direction, playerTexture)

        val transform = Transformable.at(worldX, worldZ, Direction.SOUTH, position.mapX, position.mapZ)

        val shadow = Shadow.create(worldX, worldZ, assets.obtainShadowTexture())

        return Entity()
            .add(pid)
            .add(HasMotion())
            .add(shadow)
            .add(Username(displayName))
            .add(CanOpenDoors())
            .add(CanJump())
            .add(Rank(userGroup))
            .add(Kind.PLAYER)
            .add(transform)
            .add(baseSprite)
            .add(Bicycle())
    }

    /**
     * Produces a npc entity.
     */
    fun createNpc(pid: PID, modelId: ModelId, position: MapPosition, direction: Direction): Entity {
        val tileMap = worldGrid.lookupMap(position.mapX, position.mapZ)!!

        val mapOffsetX = tileMap.properties["ox"] as Int
        val mapOffsetY = tileMap.properties["oy"] as Int

        val worldX = mapOffsetX + max(0, position.localX)
        val worldY = mapOffsetY + max(0, position.localZ)

        val npcSprites = assets.obtainOverworldNpcTexture(modelId).underlying.verticalSheetSlice()
        val baseSprite = BaseSprite.createNpcSprite(worldX, worldY, direction, npcSprites)

        val transform = Transformable.at(worldX, worldY, Direction.SOUTH, position.mapX, position.mapZ)

        val shadow = Shadow.create(worldX, worldY, assets.obtainShadowTexture())

        return Entity()
            .add(pid)
            .add(modelId)
            .add(Blocking())
            .add(HasMotion())
            .add(CanJump())
            .add(Kind.NPC)
            .add(shadow)
            .add(transform)
            .add(baseSprite)
    }

    /**
     * Produces a monster entity.
     */
    fun createMonster(
        pid: PID,
        modelId: ModelId,
        coloration: MonsterColoration,
        position: MapPosition,
        direction: Direction
    ): Entity {
        val tileMap = worldGrid.lookupMap(position.mapX, position.mapZ)!!

        val mapOffsetX = tileMap.properties["ox"] as Int
        val mapOffsetY = tileMap.properties["oy"] as Int

        val worldX = mapOffsetX + max(0, position.localX)
        val worldZ = mapOffsetY + max(0, position.localZ)

        val monsterSprites = assets.obtainOverworldMonsterTexture(modelId, coloration).underlying.verticalSheetSlice()
        val baseSprite = BaseSprite.createMonsterSprite(worldX, worldZ, direction, monsterSprites)

        val shadow = Shadow.create(worldX, worldZ, assets.obtainShadowTexture())

        val transform = Transformable.at(worldX, worldZ, Direction.SOUTH, position.mapX, position.mapZ)

        return Entity()
            .add(pid)
            .add(modelId)
            .add(HasMotion())
            .add(shadow)
            .add(CanJump())
            .add(Kind.NPC)
            .add(transform)
            .add(baseSprite)
    }

//    /**
//     * Produces an object entity.
//     */
//    fun createObject(id: Int, position: MapPosition, direction: Direction, mapX: Int, mapY: Int): Entity {
//        val baseSpriteComponent = BaseSprite.createObjectSprite(id, worldX, worldY, direction)
//
//        return Entity()
//            .add(Id(id))
//            .add(Blocking())
//            .add(Kind.OBJECT)
//            .add(Transformable.at(worldX, worldY, Direction.SOUTH, mapX, mapY))
//            .add(baseSpriteComponent)
//    }
}