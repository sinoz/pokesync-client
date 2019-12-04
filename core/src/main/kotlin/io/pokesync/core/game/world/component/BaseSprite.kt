package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.pokesync.core.game.model.Direction
import io.pokesync.core.game.model.MovementType
import io.pokesync.core.game.animation.*
import io.pokesync.core.game.world.tile.TILE_SIZE
import ktx.ashley.mapperFor

/**
 * Stores a base [Sprite] image that can be placed onto the screen.
 * @author Sino
 */
class BaseSprite private constructor(region: TextureRegion, val frames: Array<TextureRegion?>) : Sprite(region),
    Component {
    companion object {
        const val FRAME_ARRAY_SIZE = 64

        const val SCALE = 1.5F

//        fun createObjectSprite(id: Int, worldX: Int, worldY: Int, direction: Direction): BaseSprite {
//            val regions = objectSprites.getObject(id)
//            val frames = arrayOfNulls<TextureRegion?>(FRAME_ARRAY_SIZE)
//
//            frames[STANCE_SOUTH] = regions[0]
//            frames[STANCE_NORTH] = regions[0]
//            frames[STANCE_WEST] = regions[0]
//            frames[STANCE_EAST] = regions[0]
//
//            val defaultFrame = frames[STANCE_SOUTH]!!
//            val sprite = BaseSprite(defaultFrame, frames)
//
//            sprite.setPosition(worldX * TILE_SIZE, worldY * TILE_SIZE)
//
//            return sprite
//        }

        fun createNpcSprite(worldX: Int, worldY: Int, direction: Direction, regions: List<TextureRegion>): BaseSprite {
            val frames = arrayOfNulls<TextureRegion?>(FRAME_ARRAY_SIZE)

            frames[STANCE_SOUTH] = regions[11]
            frames[STANCE_NORTH] = regions[0]
            frames[STANCE_WEST] = regions[2]
            frames[STANCE_EAST] = regions[4]

            frames[WALK_NORTH_LEFT_STEP] = regions[8]
            frames[WALK_NORTH_RIGHT_STEP] = regions[0]

            frames[WALK_SOUTH_LEFT_STEP] = regions[12]
            frames[WALK_SOUTH_RIGHT_STEP] = regions[14]

            frames[WALK_EAST_LEFT_STEP] = regions[5]
            frames[WALK_EAST_RIGHT_STEP] = regions[7]

            frames[WALK_WEST_LEFT_STEP] = regions[3]
            frames[WALK_WEST_RIGHT_STEP] = regions[1]

            val defaultFrame = frames[STANCE_SOUTH]!!
            val sprite = BaseSprite(defaultFrame, frames)

            sprite.setScale(SCALE)
            sprite.setPosition(worldX * TILE_SIZE, worldY * TILE_SIZE)

            return sprite
        }

        fun createMonsterSprite(
            worldX: Int,
            worldY: Int,
            direction: Direction,
            regions: List<TextureRegion>
        ): BaseSprite {
            val frames = arrayOfNulls<TextureRegion?>(FRAME_ARRAY_SIZE)

            frames[STANCE_SOUTH] = regions[2]
            frames[STANCE_NORTH] = regions[0]
            frames[STANCE_WEST] = regions[4]
            frames[STANCE_EAST] = regions[6]

            frames[WALK_NORTH_LEFT_STEP] = regions[0]
            frames[WALK_NORTH_RIGHT_STEP] = regions[1]

            frames[WALK_SOUTH_LEFT_STEP] = regions[2]
            frames[WALK_SOUTH_RIGHT_STEP] = regions[3]

            frames[WALK_EAST_LEFT_STEP] = regions[6]
            frames[WALK_EAST_RIGHT_STEP] = regions[7]

            frames[WALK_WEST_LEFT_STEP] = regions[4]
            frames[WALK_WEST_RIGHT_STEP] = regions[5]

            val defaultFrame = frames[STANCE_SOUTH]!!
            val sprite = BaseSprite(defaultFrame, frames)

            sprite.setScale(SCALE)
            sprite.setPosition(worldX * TILE_SIZE, worldY * TILE_SIZE)

            return sprite
        }

        fun createPlayerSprite(
            worldX: Int,
            worldY: Int,
            direction: Direction,
            regions: List<TextureRegion>
        ): BaseSprite {
            val frames = arrayOfNulls<TextureRegion?>(FRAME_ARRAY_SIZE)

            frames[STANCE_SOUTH] = regions[27]
            frames[STANCE_NORTH] = regions[0]
            frames[STANCE_WEST] = regions[2]
            frames[STANCE_EAST] = regions[4]

            frames[WALK_NORTH_LEFT_STEP] = regions[11]
            frames[WALK_NORTH_RIGHT_STEP] = regions[26]

            frames[WALK_SOUTH_LEFT_STEP] = regions[28]
            frames[WALK_SOUTH_RIGHT_STEP] = regions[30]

            frames[WALK_EAST_LEFT_STEP] = regions[5]
            frames[WALK_EAST_RIGHT_STEP] = regions[7]

            frames[WALK_WEST_LEFT_STEP] = regions[3]
            frames[WALK_WEST_RIGHT_STEP] = regions[1]

            frames[RUN_STANCE_SOUTH] = regions[13]
            frames[RUN_STANCE_NORTH] = regions[8]
            frames[RUN_STANCE_WEST] = regions[17]
            frames[RUN_STANCE_EAST] = regions[21]

            frames[RUN_NORTH_LEFT_STEP] = regions[9]
            frames[RUN_NORTH_RIGHT_STEP] = regions[12]

            frames[RUN_SOUTH_LEFT_STEP] = regions[16]
            frames[RUN_SOUTH_RIGHT_STEP] = regions[14]

            frames[RUN_EAST_LEFT_STEP] = regions[23]
            frames[RUN_EAST_RIGHT_STEP] = regions[25]

            frames[RUN_WEST_LEFT_STEP] = regions[18]
            frames[RUN_WEST_RIGHT_STEP] = regions[20]

            frames[CYCLE_STANCE_SOUTH] = regions[53]
            frames[CYCLE_STANCE_NORTH] = regions[32]
            frames[CYCLE_STANCE_WEST] = regions[35]
            frames[CYCLE_STANCE_EAST] = regions[38]

            frames[CYCLE_NORTH_LEFT_STEP] = regions[50]
            frames[CYCLE_NORTH_RIGHT_STEP] = regions[43]

            frames[CYCLE_SOUTH_LEFT_STEP] = regions[54]
            frames[CYCLE_SOUTH_RIGHT_STEP] = regions[52]

            frames[CYCLE_EAST_LEFT_STEP] = regions[36]
            frames[CYCLE_EAST_RIGHT_STEP] = regions[37]

            frames[CYCLE_WEST_LEFT_STEP] = regions[34]
            frames[CYCLE_WEST_RIGHT_STEP] = regions[35]

            frames[SURF_STANCE_SOUTH] = regions[57]
            frames[SURF_STANCE_NORTH] = regions[56]
            frames[SURF_STANCE_WEST] = regions[58]
            frames[SURF_STANCE_EAST] = regions[59]

            val defaultFrame = frames[STANCE_SOUTH]!!
            val sprite = BaseSprite(defaultFrame, frames)

            sprite.setScale(SCALE)
            sprite.setPosition(worldX * TILE_SIZE, worldY * TILE_SIZE)

            return sprite
        }

        fun getStepTextureByDirection(
            component: BaseSprite,
            direction: Direction,
            movementType: MovementType,
            leftStep: Boolean
        ): TextureRegion =
            when (movementType) {
                MovementType.WALK -> getWalkStepTextureByDirection(component, direction, leftStep)
                MovementType.RUN -> getRunStepTextureByDirection(component, direction, leftStep)
                MovementType.CYCLE -> getCyclingStepTextureByDirection(component, direction, leftStep)
                else -> throw IllegalArgumentException("Unexpected $movementType")
            }

        fun getStanceTextureByDirection(
            component: BaseSprite,
            direction: Direction,
            movementType: MovementType
        ): TextureRegion =
            when (movementType) {
                MovementType.WALK -> getWalkStanceTextureByDirection(component, direction)
                MovementType.RUN -> getRunStanceTextureByDirection(component, direction)
                MovementType.CYCLE -> getCyclingStanceTextureByDirection(component, direction)
                else -> throw IllegalArgumentException("Unexpected $movementType")
            }

        private fun getRunStepTextureByDirection(
            component: BaseSprite,
            direction: Direction,
            leftStep: Boolean
        ): TextureRegion =
            with(component) {
                when (direction) {
                    Direction.SOUTH -> if (leftStep) frames[RUN_SOUTH_LEFT_STEP]!! else frames[RUN_SOUTH_RIGHT_STEP]!!
                    Direction.EAST -> if (leftStep) frames[RUN_EAST_LEFT_STEP]!! else frames[RUN_EAST_RIGHT_STEP]!!
                    Direction.NORTH -> if (leftStep) frames[RUN_NORTH_LEFT_STEP]!! else frames[RUN_NORTH_RIGHT_STEP]!!
                    Direction.WEST -> if (leftStep) frames[RUN_WEST_LEFT_STEP]!! else frames[RUN_WEST_RIGHT_STEP]!!
                }
            }

        private fun getRunStanceTextureByDirection(component: BaseSprite, direction: Direction): TextureRegion =
            with(component) {
                when (direction) {
                    Direction.SOUTH -> frames[RUN_STANCE_SOUTH]!!
                    Direction.EAST -> frames[RUN_STANCE_EAST]!!
                    Direction.NORTH -> frames[RUN_STANCE_NORTH]!!
                    Direction.WEST -> frames[RUN_STANCE_WEST]!!
                }
            }

        private fun getWalkStepTextureByDirection(
            component: BaseSprite,
            direction: Direction,
            leftStep: Boolean
        ): TextureRegion =
            with(component) {
                when (direction) {
                    Direction.SOUTH -> if (leftStep) frames[WALK_SOUTH_LEFT_STEP]!! else frames[WALK_SOUTH_RIGHT_STEP]!!
                    Direction.EAST -> if (leftStep) frames[WALK_EAST_LEFT_STEP]!! else frames[WALK_EAST_RIGHT_STEP]!!
                    Direction.NORTH -> if (leftStep) frames[WALK_NORTH_LEFT_STEP]!! else frames[WALK_NORTH_RIGHT_STEP]!!
                    Direction.WEST -> if (leftStep) frames[WALK_WEST_LEFT_STEP]!! else frames[WALK_WEST_RIGHT_STEP]!!
                }
            }

        private fun getWalkStanceTextureByDirection(component: BaseSprite, direction: Direction): TextureRegion =
            with(component) {
                when (direction) {
                    Direction.SOUTH -> frames[STANCE_SOUTH]!!
                    Direction.EAST -> frames[STANCE_EAST]!!
                    Direction.NORTH -> frames[STANCE_NORTH]!!
                    Direction.WEST -> frames[STANCE_WEST]!!
                }
            }

        fun getCyclingStepTextureByDirection(
            component: BaseSprite,
            direction: Direction,
            leftStep: Boolean
        ): TextureRegion =
            with(component) {
                when (direction) {
                    Direction.SOUTH -> if (leftStep) frames[CYCLE_SOUTH_LEFT_STEP]!! else frames[CYCLE_SOUTH_RIGHT_STEP]!!
                    Direction.EAST -> if (leftStep) frames[CYCLE_EAST_LEFT_STEP]!! else frames[CYCLE_EAST_RIGHT_STEP]!!
                    Direction.NORTH -> if (leftStep) frames[CYCLE_NORTH_LEFT_STEP]!! else frames[CYCLE_NORTH_RIGHT_STEP]!!
                    Direction.WEST -> if (leftStep) frames[CYCLE_WEST_LEFT_STEP]!! else frames[CYCLE_WEST_RIGHT_STEP]!!
                }
            }

        fun getCyclingStanceTextureByDirection(component: BaseSprite, direction: Direction): TextureRegion =
            with(component) {
                when (direction) {
                    Direction.SOUTH -> frames[CYCLE_STANCE_SOUTH]!!
                    Direction.EAST -> frames[CYCLE_STANCE_EAST]!!
                    Direction.NORTH -> frames[CYCLE_STANCE_NORTH]!!
                    Direction.WEST -> frames[CYCLE_STANCE_WEST]!!
                }
            }

        fun getSurfStanceTextureByDirection(component: BaseSprite, direction: Direction): TextureRegion =
            with(component) {
                when (direction) {
                    Direction.SOUTH -> frames[SURF_STANCE_SOUTH]!!
                    Direction.EAST -> frames[SURF_STANCE_EAST]!!
                    Direction.NORTH -> frames[SURF_STANCE_NORTH]!!
                    Direction.WEST -> frames[SURF_STANCE_WEST]!!
                }
            }

        val MAPPER = mapperFor<BaseSprite>()
    }
}