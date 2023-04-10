package io.github.anvell.either.binding.internal

import kotlin.coroutines.cancellation.CancellationException

internal actual open class BlankCancellationException : CancellationException() {

    override fun fillInStackTrace(): Throwable {
        stackTrace = emptyArray()
        return this
    }
}
