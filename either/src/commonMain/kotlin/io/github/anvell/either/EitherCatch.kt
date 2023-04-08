package io.github.anvell.either

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] and returns its encapsulated result as [Right]
 * if invocation was successful, catching any [Throwable] exception that was
 * thrown from the [block] function execution and encapsulating it as [Left].
 *
 * In order to avoid breaking structured concurrency of coroutines [CancellationException]
 * is re-thrown.
 */
public inline fun <R> eitherCatch(block: () -> R): Either<Throwable, R> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return try {
        Right(block())
    } catch (e: Throwable) {
        when (e) {
            is TimeoutCancellationException -> Left(e)
            is CancellationException -> throw e
            else -> Left(e)
        }
    }
}
