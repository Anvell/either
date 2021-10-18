@file:Suppress("unused")

package io.github.anvell.either.async

import io.github.anvell.either.Either
import io.github.anvell.either.Left
import io.github.anvell.either.Right
import io.github.anvell.either.fold
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Allows to compose a set of [Either] values in an imperative way
 * using suspendable [bind][EitherCoroutineScope.bind] function.
 *
 * Result is returned as [Deferred] object.
 */
inline fun <L : Any, R> CoroutineScope.eitherAsync(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend EitherCoroutineScope<L>.() -> R
): Deferred<Either<L, R>> {
    return async(start = start) {
        with(EitherCoroutineScopeImpl<L>(coroutineContext)) {
            try {
                Right(block())
            } catch (e: BindingCoroutineException) {
                Left(left)
            }
        }
    }
}

/**
 * Allows to compose a set of [Either] values in an imperative way
 * using suspendable [bind][EitherCoroutineScope.bind] function.
 */
suspend inline fun <L : Any, R> either(
    crossinline block: EitherCoroutineScope<L>.() -> R
): Either<L, R> = coroutineScope {
    with(EitherCoroutineScopeImpl<L>(coroutineContext)) {
        try {
            Right(block())
        } catch (e: BindingCoroutineException) {
            Left(left)
        }
    }
}

interface EitherCoroutineScope<L : Any> : CoroutineScope {
    fun <R> Either<L, R>.bind(): R
    suspend fun <R> Deferred<Either<L, R>>.bind(): R
}

@PublishedApi
internal class EitherCoroutineScopeImpl<L : Any>(
    override val coroutineContext: CoroutineContext
) : EitherCoroutineScope<L> {
    lateinit var left: L

    override fun <R> Either<L, R>.bind(): R {
        return fold(
            left = {
                left = it
                throw BindingCoroutineException()
            },
            right = { it }
        )
    }

    override suspend fun <R> Deferred<Either<L, R>>.bind(): R {
        return await().bind()
    }
}

private class BindingCoroutineException : Exception()
