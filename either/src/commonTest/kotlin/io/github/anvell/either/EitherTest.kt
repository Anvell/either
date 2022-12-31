package io.github.anvell.either

import io.github.anvell.either.resources.TestExceptions.TestErrorOne
import kotlin.test.Test
import kotlin.test.assertFailsWith

class EitherTest {

    @Test
    fun leftShouldThrowUnderlyingExceptionWhenUnwrapped() {
        val a: Either<Exception, Int> = Left(TestErrorOne)

        assertFailsWith(TestErrorOne::class) { a.unwrap() }
    }
}
