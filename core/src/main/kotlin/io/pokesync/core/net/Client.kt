package io.pokesync.core.net

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.pokesync.core.message.Message
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.ByteWriteChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import java.net.ConnectException
import java.net.InetSocketAddress

/**
 * A client that can connect to a remote server. The internal state of this
 * implementation is not thread-safe and attempts to connect to remote
 * endpoints should NOT occur across multiple threads. Either create separate
 * [Client] instances or make use of Thread-Local's.
 * @author Sino
 */
class Client private constructor(private val bldr: TcpSocketBuilder, val codec: MessageCodec) : CanConnectToRemote {
    /**
     * A write message.
     */
    sealed class WriteMsg {
        /**
         * An internal client [Message] to send a message across the wire.
         */
        data class Send(val message: Message) : WriteMsg()

        /**
         * A message to flush all of the buffered contents.
         */
        object Flush : WriteMsg()
    }

    /**
     * Used to log information to console and/or to file.
     */
    private val log = KotlinLogging.logger {}

    /**
     * The underlying socket.
     */
    private var socket: Socket? = null

    /**
     * The upstream channel of bytes.
     */
    private var reader: ByteReadChannel? = null

    /**
     * The downstream channel of bytes.
     */
    private var writer: ByteWriteChannel? = null

    /**
     * The reading job.
     */
    private var readingJob: Job? = null

    /**
     * The writing job.
     */
    private var writingJob: Job? = null

    /**
     * A mutex.
     */
    private val mutex = Mutex()

    /**
     * A buffer of messages that have been read upstream.
     */
    private val upstreamMessageBuffer = Channel<Message>(Channel.UNLIMITED)

    /**
     * A buffer of messages to write downstream.
     */
    private val downstreamMessageBuffer = Channel<WriteMsg>(Channel.UNLIMITED)

    /**
     * Attempts to connect to the specified [RemoteEndpoint].
     */
    override suspend fun connect(endpoint: RemoteEndpoint, timeout: Long): ConnectResponse = mutex.withLock {
        try {
            socket = tryConnect(endpoint.address, timeout) ?: return ConnectResponse.TimedOut

            reader = socket!!.openReadChannel()
            writer = socket!!.openWriteChannel(autoFlush = false)

            readingJob = GlobalScope.launch(Dispatchers.IO) { read() }
            writingJob = GlobalScope.launch(Dispatchers.IO) { write() }
        } catch (e: ConnectException) {
            return ConnectResponse.Refused
        } catch (e: Throwable) {
            return ConnectResponse.Otherwise(e)
        }

        return ConnectResponse.Ok
    }

    /**
     * Attempts to connect to the specified [InetSocketAddress]. Returns null
     * if the connection timed out.
     */
    private suspend fun tryConnect(address: InetSocketAddress, timeout: Long): Socket? =
        withTimeoutOrNull(timeout) { bldr.connect(address) }

    /**
     * Spawns a coroutine process that is to continuously read [Packet]s
     * from the [reader] channel until the [socket] is closed.
     */
    private suspend fun read() {
        try {
            while (!socket!!.isClosed) {
                val packet = Packet.fork(reader!!)

                val decoder = codec.decoders[packet.kind]
                val message = decoder?.decode(packet)
                if (message != null) {
                    upstreamMessageBuffer.offer(message)
                } else {
                    throw Exception()
                }
            }
        } catch (e: Throwable) {
            log.error("Failed to read a message with cause:", e)
            close()
        }
    }

    /**
     * Spawns a coroutine process that is to continuously write [Packet]s
     * to the [writer] channel until the [socket] is closed.
     */
    private suspend fun write() {
        try {
            for (command in downstreamMessageBuffer) {
                when (command) {
                    is WriteMsg.Send -> {
                        val encoder = codec.encoders[command.message::class] as MessageToPacketEncoder<Message>?
                        val packet = encoder?.encode(command.message)
                        if (packet == null) {
                            throw Exception()
                        } else {
                            Packet.join(packet, writer!!)
                        }
                    }

                    is WriteMsg.Flush -> {
                        writer!!.flush()
                    }
                }
            }
        } catch (e: Throwable) {
            log.error("Failed to write a message with cause:", e)
            close()
        }
    }

    /**
     * Closes the connection, if established.
     */
    override suspend fun close() = mutex.withLock {
        readingJob?.cancelAndJoin()
        writingJob?.cancelAndJoin()

        readingJob = null
        writingJob = null

        socket?.close()
        socket = null
    }

    /**
     * Waits for a [Message] from the other side. This function suspends
     * if the channel's empty.
     */
    override suspend fun receive(): Message {
        return upstreamMessageBuffer.receive()
    }

    /**
     * Returns whether a connection has been established and is still active.
     */
    override fun isConnected(): Boolean {
        return socket != null
    }

    /**
     * Polls a [Message] from the other side. Returns null if the
     * channel's empty.
     */
    override fun poll(): Message? {
        return upstreamMessageBuffer.poll()
    }

    /**
     * Attempts to flush queued up bytes to the socket.
     */
    override fun flush() {
        downstreamMessageBuffer.offer(WriteMsg.Flush)
    }

    /**
     * Attempts to write the given [Message]. May automatically flush the given
     * message and all of its unflushed contents that were written before.
     * This function may suspend.
     */
    override fun send(message: Message, immediateFlush: Boolean) {
        downstreamMessageBuffer.offer(WriteMsg.Send(message))
        if (immediateFlush) {
            flush()
        }
    }

    companion object {
        /**
         * Constructs a [Client].
         */
        fun create(codec: MessageCodec): Client =
            Client(aSocket(ActorSelectorManager(Dispatchers.IO)).tcp(), codec)
    }
}