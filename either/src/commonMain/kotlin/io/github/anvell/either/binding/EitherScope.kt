package io.github.anvell.either.binding

import io.github.anvell.either.Either

public interface EitherScope<L : Any> {
    public fun <R> Either<L, R>.bind(): R
}
