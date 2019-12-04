package io.pokesync.lib.effect

import arrow.core.Either
import arrow.effects.IO

/**
 * An alias for `flatMap`.
 */
fun <A, B> IO<A>.bind(f: (A) -> IO<B>): IO<B> =
    flatMap(f)

/**
 * Binds this sequence to the next given effect of `f`, without
 * passing on the result from the previous effect to the next one.
 */
fun <A, B> IO<A>.andThen(f: IO<B>): IO<B> =
    flatMap { f }

/**
 * Lazily lifts the given [Either] into an [IO].
 */
fun <A, B> fromEither(e: () -> Either<A, B>): IO<B> =
    IO.defer {
        when (val result = e()) {
            is Either.Right -> IO.just(result.b)
            is Either.Left  -> IO.raiseError(Exception("${result.a}"))
        }
    }