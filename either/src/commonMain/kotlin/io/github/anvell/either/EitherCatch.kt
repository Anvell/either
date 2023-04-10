package io.github.anvell.either

import io.github.anvell.either.internal.isFatalOrCancellation
import kotlinx.coroutines.CancellationException
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
    } catch (t: Throwable) {
        if (isFatalOrCancellation(t)) {
            throw t
        } else {
            Left(t)
        }
    }
}
