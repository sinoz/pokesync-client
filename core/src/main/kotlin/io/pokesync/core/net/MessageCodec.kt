package io.pokesync.core.net

import io.pokesync.core.message.*
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import kotlin.reflect.KClass

/**
 * A group of message decoders and encoders.
 * @author Sino
 */
data class MessageCodec(
    val decoders: Object2ObjectMap<Packet.Kind, PacketToMessageDecoder<*>>,
    val encoders: Object2ObjectMap<KClass<*>, MessageToPacketEncoder<*>>
) {
    companion object {
        /**
         * Builds up a [MessageCodec].
         */
        class CodecBuilder {
            private val decoders = Object2ObjectArrayMap<Packet.Kind, PacketToMessageDecoder<*>>()

            private val encoders = Object2ObjectArrayMap<KClass<*>, MessageToPacketEncoder<*>>()

            /**
             * Includes the given [PacketToMessageDecoder].
             */
            fun <M : Message> include(kind: Packet.Kind, decoder: PacketToMessageDecoder<M>) {
                decoders[kind] = decoder
            }

            /**
             * Includes the given [MessageToPacketEncoder].
             */
            fun <M : Message> include(type: KClass<M>, encoder: MessageToPacketEncoder<M>) {
                encoders[type] = encoder
            }

            fun build(): MessageCodec =
                MessageCodec(decoders, encoders)
        }

        /**
         * Constructs a [MessageCodec].
         */
        fun codec(f: CodecBuilder.() -> Unit): MessageCodec {
            val bldr = CodecBuilder()
            f(bldr)
            return bldr.build()
        }

        /**
         * Creates a default [MessageCodec].
         */
        fun default(): MessageCodec =
            codec {
                /**
                 * Outgoing: Client -> Server
                 */
                include(RequestLogin::class, RequestLogin.encoder())
                include(CreateCharacter::class, CreateCharacter.encoder())
                include(SelectCharacter::class, SelectCharacter.encoder())
                include(AttachFollower::class, AttachFollower.encoder())
                include(ClearFollower::class, ClearFollower.encoder())
                include(ChangeMovementType::class, ChangeMovementType.encoder())
                include(ContinueDialogue::class, ContinueDialogue.encoder())
                include(FaceDirection::class, FaceDirection.encoder())
                include(InteractWithEntity::class, InteractWithEntity.encoder())
                include(MoveAvatar::class, MoveAvatar.encoder())
                include(SelectPlayerOption::class, SelectPlayerOption.encoder())
                include(SwitchPartySlots::class, SwitchPartySlots.encoder())
                include(SubmitChatMessage::class, SubmitChatMessage.encoder())
                include(SubmitChatCommand::class, SubmitChatCommand.encoder())
                include(SelectChatChannel::class, SelectChatChannel.encoder())
                include(ClickTeleport::class, ClickTeleport.encoder())

                /**
                 * Incoming: Server -> Client
                 */
                include(Packet.Kind.LOGIN_SUCCESS, LoginSuccess.decoder())
                include(Packet.Kind.ACCOUNT_DISABLED, AccountDisabled.decoder())
                include(Packet.Kind.ALREADY_LOGGED_IN, AlreadyLoggedIn.decoder())
                include(Packet.Kind.INVALID_CREDENTIALS, InvalidCredentials.decoder())
                include(Packet.Kind.UNABLE_TO_FETCH_PROFILE, UnableToFetchProfile.decoder())
                include(Packet.Kind.WORLD_FULL, WorldFull.decoder())
                include(Packet.Kind.AUTH_TIMED_OUT, AuthenticationTimedOut.decoder())
                include(Packet.Kind.MAP_REFRESH, MapRefreshed.decoder())
                include(Packet.Kind.DISPLAY_CHAT_MSG, DisplayChatMessage.decoder())
                include(Packet.Kind.CLOSE_DIALOGUE, CloseDialogue.decoder())
                include(Packet.Kind.ENTITY_UPDATE, EntityUpdate.decoder())
                include(Packet.Kind.MOVE_ORTHO_CAMERA, MoveOrthographicCamera.decoder())
                include(Packet.Kind.RESET_ORTHO_CAMERA, ResetOrthographicCamera.decoder())
                include(Packet.Kind.SET_DONATOR_PTS, SetDonatorPoints.decoder())
                include(Packet.Kind.SET_PARTY_SLOT, SetPartySlot.decoder())
                include(Packet.Kind.SET_POKEDOLLAR, SetPokeDollars.decoder())
                include(Packet.Kind.SET_SERVER_TIME, SetServerTime.decoder())
                include(Packet.Kind.SWITCH_CHAT_CHAN, SwitchToChatChannel.decoder())
            }
    }
}