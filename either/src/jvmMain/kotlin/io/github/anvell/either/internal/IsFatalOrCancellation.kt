package io.github.anvell.either.internal

import kotlinx.coroutines.TimeoutCancellationException
import kotlin.coroutines.cancellation.CancellationException

@PublishedApi
internal actual fun isFatalOrCancellation(t: Throwable): Boolean {
    return when (t) {
        is TimeoutCancellationException -> false
        is VirtualMachineError,
        is ThreadDeath,
        is InterruptedException,
        is LinkageError,
        is CancellationException -> true
        else -> false
    }
}
