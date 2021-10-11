@file:Suppress("unused")

package io.github.anvell.either

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

inline fun <R> eitherCatch(block: () -> R): Either<Throwable, R> {
    return try {
        Right(block())
    } catch (e: Throwable) {
        Left(e)
    }
}

inline fun <L, R, V> Either<L, R>.map(transform: (R) -> V): Either<L, V> {
    return when (this) {
        is Left -> this
        is Right -> Right(transform(value))
    }
}

inline fun <L, R, V> Either<L, R>.mapLeft(transform: (L) -> V): Either<V, R> {
    return when (this) {
        is Left -> Left(transform(value))
        is Right -> this
    }
}

inline fun <L, R, V> Either<L, R>.mapRight(transform: (R) -> V): Either<L, V> {
    return when (this) {
        is Left -> this
        is Right -> Right(transform(value))
    }
}

inline fun <L, R, E, K> Either<L, R>.mapBoth(left: (L) -> E, right: (R) -> K): Either<E, K> {
    return when (this) {
        is Left -> Left(left(value))
        is Right -> Right(right(value))
    }
}

inline fun <L, R, V> Either<L, R>.flatMap(transform: (R) -> Either<L, V>): Either<L, V> {
    return when (this) {
        is Left -> this
        is Right -> transform(value)
    }
}

inline fun <L, R> Either<L, R>.or(block: (L) -> Either<L, R>): Either<L, R> {
    return when (this) {
        is Left -> block(value)
        is Right -> this
    }
}

inline fun <L, R, V> Either<L, R>.fold(left: (L) -> V, right: (R) -> V): V {
    return when (this) {
        is Left -> left(value)
        is Right -> right(value)
    }
}

fun <L, R> Either<L, R>.left(): L? {
    return when (this) {
        is Left -> value
        is Right -> null
    }
}

fun <L, R> Either<L, R>.right(): R? {
    return when (this) {
        is Left -> null
        is Right -> value
    }
}

fun <L, R> Either<L, R>.unwrap(): R {
    return when (this) {
        is Left -> error("Failed to unwrap the value!")
        is Right -> value
    }
}

inline fun <L, R> Either<L, R>.unwrapOrElse(transform: (L) -> R): R {
    return when (this) {
        is Left -> transform(value)
        is Right -> value
    }
}
