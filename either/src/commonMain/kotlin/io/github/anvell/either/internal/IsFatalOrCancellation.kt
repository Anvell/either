package io.github.anvell.either.internal

@PublishedApi
internal expect fun isFatalOrCancellation(t: Throwable): Boolean
