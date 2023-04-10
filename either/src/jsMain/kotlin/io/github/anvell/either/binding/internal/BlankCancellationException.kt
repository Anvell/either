package io.github.anvell.either.binding.internal

import kotlin.coroutines.cancellation.CancellationException

internal actual open class BlankCancellationException : CancellationException()
