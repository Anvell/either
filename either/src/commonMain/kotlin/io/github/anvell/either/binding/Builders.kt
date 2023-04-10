@file:Suppress("unused")

package io.github.anvell.either.binding

import io.github.anvell.either.Either
import io.github.anvell.either.Left
import io.github.anvell.either.Right
import io.github.anvell.either.binding.internal.BindingCancellationException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Allows to compose a set of [Either] values in an imperative way
 * using [bind][EitherScope.bind] function.
 */
public inline fun <L : Any, R> either(
    @BuilderInference block: EitherScope<L>.() -> R
): Either<L, R> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return with(EitherScopeImpl<L>()) {
        try {
            Right(block())
        } catch (e: BindingCancellationException) {
            Left(left)
        }
    }
}
