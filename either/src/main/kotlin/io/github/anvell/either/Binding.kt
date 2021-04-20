@file:Suppress("unused")

package io.github.anvell.either

fun <L : Any, R> either(
    block: EitherScope<L>.() -> R
): Either<L, R> = with(EitherScopeImpl<L>()) {
    try {
        Right(block())
    } catch (e: BindingException) {
        Left(left)
    }
}

interface EitherScope<L : Any> {
    fun <R> Either<L, R>.bind(): R
}

private class EitherScopeImpl<L : Any> : EitherScope<L> {
    lateinit var left: L

    override fun <R> Either<L, R>.bind(): R = fold(
        left = {
            left = it
            throw BindingException()
        },
        right = { it }
    )
}

private class BindingException : Exception()
