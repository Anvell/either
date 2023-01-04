package io.github.anvell.either

import io.github.anvell.either.resources.TestExceptions.TestErrorOne
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class EitherTest : StringSpec({

    "Left should throw underlying exception when unwrapped" {
        val a: Either<Exception, Int> = Left(TestErrorOne)

        shouldThrow<TestErrorOne> { a.unwrap() }
    }
})
