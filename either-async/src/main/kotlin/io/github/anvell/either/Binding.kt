@file:Suppress("unused")

package io.github.anvell.either

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
            } catch (e: BindingException) {
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
                throw BindingException()
            },
            right = { it }
        )
    }

    override suspend fun <R> Deferred<Either<L, R>>.bind(): R {
        return await().bind()
    }
}

private class BindingException : Exception()
