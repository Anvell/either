package io.github.anvell.either.binding.internal

import io.github.anvell.either.Either
import io.github.anvell.either.Left
import io.github.anvell.either.Right
import io.github.anvell.either.binding.EitherScope

@PublishedApi
internal class EitherScopeImpl<L : Any> : EitherScope<L> {
    lateinit var left: L

    override fun <R> Either<L, R>.bind(): R = when (this) {
        is Left -> raise(value)
        is Right -> value
    }

    override fun raise(value: L): Nothing {
        if (::left.isInitialized.not()) {
            left = value
        }
        throw BindingCancellationException()
    }
}
