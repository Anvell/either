@file:Suppress("unused")

package io.github.anvell.either

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException

/**
 * Either monad implementation.
 *
 * @param L usually represents negative value.
 * @param R usually represents positive value.
 */
sealed class Either<out L, out R> {
    abstract operator fun component1(): L?
    abstract operator fun component2(): R?
}

class Left<out L>(val value: L) : Either<L, Nothing>() {
    override fun component1() = value
    override fun component2() = null

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Left<*>
        if (value != other.value) return false
        return true
    }
}

class Right<out R>(val value: R) : Either<Nothing, R>() {
    override fun component1() = null
    override fun component2() = value

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Right<*>
        if (value != other.value) return false
        return true
    }
}

/**
 * Calls the specified function [block] and returns its encapsulated result as [Right]
 * if invocation was successful, catching any [Throwable] exception that was
 * thrown from the [block] function execution and encapsulating it as [Left].
 *
 * In order to avoid breaking structured concurrency of coroutines [CancellationException]
 * is re-thrown.
 */
inline fun <R> eitherCatch(block: () -> R): Either<Throwable, R> {
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

/**
 * Returns result of the given [transform] applied to the encapsulated [R] value
 * if this instance represents [Right] or the original encapsulated [L] value
 * if this instance is [Left].
 *
 * @see mapRight
 */
inline fun <L, R, V> Either<L, R>.map(transform: (R) -> V) = mapRight(transform)

/**
 * Returns result of the given [transform] applied to the encapsulated [L] value
 * if this instance represents [Left] or the original encapsulated [R] value
 * if this instance is [Right].
 */
inline fun <L, R, V> Either<L, R>.mapLeft(transform: (L) -> V): Either<V, R> {
    return when (this) {
        is Left -> Left(transform(value))
        is Right -> this
    }
}

/**
 * Returns result of the given [transform] applied to the encapsulated [R] value
 * if this instance represents [Right] or the original encapsulated [L] value
 * if this instance is [Left].
 */
inline fun <L, R, V> Either<L, R>.mapRight(transform: (R) -> V): Either<L, V> {
    return when (this) {
        is Left -> this
        is Right -> Right(transform(value))
    }
}

/**
 * Returns result of the given [left] block applied to the encapsulated [L] value
 * if this instance represents [Left] or result of the [right] block applied
 * to the encapsulated [R] value if this instance is [Right].
 */
inline fun <L, R, E, K> Either<L, R>.mapBoth(left: (L) -> E, right: (R) -> K): Either<E, K> {
    return when (this) {
        is Left -> Left(left(value))
        is Right -> Right(right(value))
    }
}

/**
 * Returns result of the given [transform] applied to the encapsulated [R] value
 * if this instance represents [Right] or the original encapsulated [L] value
 * if this instance is [Left].
 */
inline fun <L, R, V> Either<L, R>.flatMap(transform: (R) -> Either<L, V>): Either<L, V> {
    return when (this) {
        is Left -> this
        is Right -> transform(value)
    }
}

/**
 * Returns result of the given [transform] applied to the encapsulated [L] value
 * if this instance represents [Left] or the original encapsulated [R] value
 * if this instance is [Right].
 */
inline fun <L, R> Either<L, R>.or(transform: (L) -> Either<L, R>): Either<L, R> {
    return when (this) {
        is Left -> transform(value)
        is Right -> this
    }
}

/**
 * Returns result of [left] for the encapsulated value
 * if this instance represents [Left] or the result of [right]
 * if it is [Right].
 */
inline fun <L, R, V> Either<L, R>.fold(left: (L) -> V, right: (R) -> V): V {
    return when (this) {
        is Left -> left(value)
        is Right -> right(value)
    }
}

/**
 * Performs [action] on encapsulated [L] value
 * if this instance represents [Left].
 *
 * @return unchanged [Either].
 */
inline fun <L, R> Either<L, R>.onLeft(action: (L) -> Unit): Either<L, R> {
    if (this is Left) action(value)
    return this
}

/**
 * Performs [action] on encapsulated [R] value
 * if this instance represents [Right].
 *
 * @return unchanged [Either].
 */
inline fun <L, R> Either<L, R>.onRight(action: (R) -> Unit): Either<L, R> {
    if (this is Right) action(value)
    return this
}

/**
 * Get encapsulated value [L] if this instance represents [Left] or null.
 *
 * @return optional [L] value.
 */
fun <L, R> Either<L, R>.left(): L? {
    return when (this) {
        is Left -> value
        is Right -> null
    }
}

/**
 * Get encapsulated value [R] if this instance represents [Right] or null.
 *
 * @return optional [R] value
 */
fun <L, R> Either<L, R>.right(): R? {
    return when (this) {
        is Left -> null
        is Right -> value
    }
}

/**
 * Try to get encapsulated value [R] or throw.
 */
fun <L, R> Either<L, R>.unwrap(): R {
    return when (this) {
        is Left -> when (value) {
            is Throwable -> throw value
            else -> error("Failed to unwrap the value!")
        }
        is Right -> value
    }
}

/**
 * Get encapsulated value [R] or provide fallback with [transform].
 */
inline fun <L, R> Either<L, R>.unwrapOrElse(transform: (L) -> R): R {
    return when (this) {
        is Left -> transform(value)
        is Right -> value
    }
}
