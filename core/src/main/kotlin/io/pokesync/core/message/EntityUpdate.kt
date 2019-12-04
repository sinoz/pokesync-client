package io.pokesync.core.message

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.pokesync.core.account.UserGroup
import io.pokesync.core.game.model.*
import io.pokesync.core.game.world.component.Kind
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.core.game.world.component.PID
import io.pokesync.core.net.Packet
import io.pokesync.core.net.PacketToMessageDecoder
import io.pokesync.lib.bytes.readCString

/**
 * A collection of entity updates.
 * @author Sino
 */
data class EntityUpdate(val updates: List<Update>, val additions: List<AdditionUpdate>) : Message {
    /**
     * A status update of an entity.
     */
    sealed class StatusUpdate {
        /**
         * A player's status on the overworld.
         */
        data class PlayerStatus(
            val inBattle: Boolean,
            val gender: Gender,
            val displayName: DisplayName,
            val userGroup: UserGroup
        ) : StatusUpdate()

        /**
         * An object's status on the overworld.
         */
        data class ObjectStatus(
            val id: ModelId
        ) : StatusUpdate()

        /**
         * A npc's status on the overworld.
         */
        data class NpcStatus(
            val id: ModelId
        ) : StatusUpdate()

        /**
         * A monster's status on the overworld.
         */
        data class MonsterStatus(
            val id: ModelId,
            val coloration: MonsterColoration
        ) : StatusUpdate()
    }

    /**
     * A type of position update.
     */
    sealed class PositionUpdate {
        object NoOp : PositionUpdate()
        object Removal : PositionUpdate()

        object JustStatusChange : PositionUpdate()

        data class Reface(val direction: Direction) : PositionUpdate()
        data class TakeStep(val direction: Direction) : PositionUpdate()

        data class Teleport(val position: MapPosition) : PositionUpdate()

        data class ChangedMovementType(val movementType: MovementType) : PositionUpdate()
    }

    /**
     * An update of an already added entity in the user's viewport.
     */
    data class Update(val positionUpdate: PositionUpdate, val status: StatusUpdate?)

    /**
     * An update to add a new entity to the user's viepwort.
     */
    sealed class AdditionUpdate {
        /**
         * An [AdditionUpdate] to add a new player entity to the user's viewport.
         */
        data class AddPlayer(
            val pid: PID,
            val position: MapPosition,
            val direction: Direction,
            val movementType: MovementType,
            val status: StatusUpdate.PlayerStatus
        ) : AdditionUpdate()

        /**
         * An [AdditionUpdate] to add a new npc entity to the user's viewport.
         */
        data class AddNpc(
            val pid: PID,
            val position: MapPosition,
            val direction: Direction,
            val status: StatusUpdate.NpcStatus
        ) : AdditionUpdate()

        /**
         * An [AdditionUpdate] to add a new object entity to the user's viewport.
         */
        data class AddObject(
            val pid: PID,
            val position: MapPosition,
            val direction: Direction,
            val status: StatusUpdate.ObjectStatus
        ) : AdditionUpdate()

        /**
         * An [AdditionUpdate] to add a new monster entity to the user's viewport.
         */
        data class AddMonster(
            val pid: PID,
            val position: MapPosition,
            val direction: Direction,
            val status: StatusUpdate.MonsterStatus
        ) : AdditionUpdate()
    }

    companion object {
        /**
         * An empty list of [Update]s.
         */
        private val NO_UPDATES = mutableListOf<Update>()

        /**
         * An empty list of [AdditionUpdate]s.
         */
        private val NO_ADDITIONS = mutableListOf<AdditionUpdate>()

        /**
         * Creates a [PacketToMessageDecoder] to decode [EntityUpdate]
         * messages.
         */
        fun decoder(): PacketToMessageDecoder<EntityUpdate> =
            object : PacketToMessageDecoder<EntityUpdate> {
                override fun decode(packet: Packet): EntityUpdate {
                    val buffer = Unpooled.wrappedBuffer(packet.bytes.asReadOnlyByteBuffer())

                    val updateCount = buffer.readUnsignedShort()
                    val updates = if (updateCount > 0) mutableListOf() else NO_UPDATES
                    for (i in 0 until updateCount) {
                        val positionUpdate = when (buffer.readUnsignedByte().toInt()) {
                            0 -> PositionUpdate.NoOp
                            1 -> PositionUpdate.Removal

                            2 -> PositionUpdate.Reface(Direction.fromId(buffer.readUnsignedByte().toInt()))
                            3 -> PositionUpdate.TakeStep(Direction.fromId(buffer.readUnsignedByte().toInt()))

                            4 -> {
                                val mapX = buffer.readUnsignedShort()
                                val mapZ = buffer.readUnsignedShort()

                                val localX = buffer.readUnsignedShort()
                                val localZ = buffer.readUnsignedShort()

                                PositionUpdate.Teleport(MapPosition(mapX, mapZ, localX, localZ))
                            }

                            5 -> PositionUpdate.ChangedMovementType(MovementType.fromId(buffer.readUnsignedByte().toInt()))
                            6 -> PositionUpdate.JustStatusChange

                            else -> throw Exception()
                        }

                        val hasStatusUpdate = buffer.readBoolean()
                        if (!hasStatusUpdate) {
                            updates.add(Update(positionUpdate, null))
                        } else {
                            val entityKind = Kind.fromId(buffer.readUnsignedByte().toInt())
                            val statusUpdate = buffer.readStatusUpdate(entityKind)

                            val update = when (entityKind) {
                                Kind.PLAYER -> {
                                    Update(positionUpdate, statusUpdate as StatusUpdate.PlayerStatus)
                                }

                                Kind.NPC -> {
                                    Update(positionUpdate, statusUpdate as StatusUpdate.NpcStatus)
                                }

                                Kind.MONSTER -> {
                                    Update(positionUpdate, statusUpdate as StatusUpdate.MonsterStatus)
                                }

                                Kind.OBJECT -> {
                                    Update(positionUpdate, statusUpdate as StatusUpdate.ObjectStatus)
                                }
                            }

                            updates.add(update)
                        }
                    }

                    val additionCount = buffer.readUnsignedShort()
                    val additions = if (additionCount > 0) mutableListOf() else NO_ADDITIONS
                    for (i in 0 until additionCount) {
                        val pid = PID(buffer.readUnsignedShort())

                        val direction = Direction.fromId(buffer.readUnsignedByte().toInt())
                        val movementType = MovementType.fromId(buffer.readUnsignedByte().toInt())

                        val mapX = buffer.readUnsignedShort()
                        val mapZ = buffer.readUnsignedShort()

                        val localX = buffer.readUnsignedShort()
                        val localZ = buffer.readUnsignedShort()

                        val position = MapPosition(mapX, mapZ, localX, localZ)

                        val entityKind = Kind.fromId(buffer.readUnsignedByte().toInt())
                        val statusUpdate = buffer.readStatusUpdate(entityKind)

                        val additionUpdate = when (entityKind) {
                            Kind.PLAYER -> {
                                AdditionUpdate.AddPlayer(
                                    pid,
                                    position,
                                    direction,
                                    movementType,
                                    statusUpdate as StatusUpdate.PlayerStatus
                                )
                            }

                            Kind.NPC -> {
                                AdditionUpdate.AddNpc(pid, position, direction, statusUpdate as StatusUpdate.NpcStatus)
                            }

                            Kind.MONSTER -> {
                                AdditionUpdate.AddMonster(
                                    pid,
                                    position,
                                    direction,
                                    statusUpdate as StatusUpdate.MonsterStatus
                                )
                            }

                            Kind.OBJECT -> {
                                AdditionUpdate.AddObject(
                                    pid,
                                    position,
                                    direction,
                                    statusUpdate as StatusUpdate.ObjectStatus
                                )
                            }
                        }

                        additions.add(additionUpdate)
                    }

                    return EntityUpdate(updates, additions)
                }
            }

        /**
         * Reads a [StatusUpdate] from the [ByteBuf] for the specified entity [Kind].
         */
        private fun ByteBuf.readStatusUpdate(kind: Kind): StatusUpdate =
            when (kind) {
                Kind.PLAYER -> {
                    val inBattle = readBoolean()
                    val gender = Gender.fromId(readUnsignedByte().toInt())!!
                    val displayName = DisplayName(readCString())
                    val userGroup = UserGroup.fromId(readUnsignedByte().toInt())

                    StatusUpdate.PlayerStatus(inBattle, gender, displayName, userGroup)
                }

                Kind.NPC -> {
                    StatusUpdate.NpcStatus(ModelId(readUnsignedShort()))
                }

                Kind.MONSTER -> {
                    StatusUpdate.MonsterStatus(ModelId(readUnsignedShort()), MonsterColoration.fromId(readUnsignedByte().toInt()))
                }

                Kind.OBJECT -> {
                    StatusUpdate.ObjectStatus(ModelId(readUnsignedShort()))
                }
            }
    }
}