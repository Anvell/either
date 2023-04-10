@file:Suppress("unused")

package io.github.anvell.either

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
        } catch (e: BindingException) {
            Left(left)
        }
    }
}

public interface EitherScope<L : Any> {
    public fun <R> Either<L, R>.bind(): R
}

@PublishedApi
internal class EitherScopeImpl<L : Any> : EitherScope<L> {
    lateinit var left: L

    override fun <R> Either<L, R>.bind(): R = when(this) {
        is Left -> {
            if (::left.isInitialized.not()) {
                left = value
            }
            throw BindingException()
        }
        is Right -> value
    }
}

private class BindingException : Exception()
