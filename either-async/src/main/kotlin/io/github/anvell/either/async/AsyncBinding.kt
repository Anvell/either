@file:Suppress("unused")

package io.github.anvell.either.async

import io.github.anvell.either.Either
import io.github.anvell.either.Left
import io.github.anvell.either.Right
import io.github.anvell.either.fold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

fun <L : Any, R> CoroutineScope.eitherAsync(
    block: suspend EitherCoroutineScope<L>.() -> R
): Deferred<Either<L, R>> {
    return async {
        with(EitherCoroutineScopeImpl<L>(coroutineContext)) {
            try {
                Right(block())
            } catch (e: BindingCoroutineException) {
                Left(left)
            }
        }
    }
}

interface EitherCoroutineScope<L : Any> : CoroutineScope {
    fun <R> Either<L, R>.bind(): R
    suspend fun <R> Deferred<Either<L, R>>.bind(): R
}

private class EitherCoroutineScopeImpl<L : Any>(
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
