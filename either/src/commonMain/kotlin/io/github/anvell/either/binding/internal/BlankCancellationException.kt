package io.github.anvell.either.binding.internal

import kotlin.coroutines.cancellation.CancellationException

internal expect open class BlankCancellationException() : CancellationException
