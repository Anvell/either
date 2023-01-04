package io.github.anvell.either

import io.github.anvell.either.resources.TestExceptions.TestErrorOne
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EitherTest : StringSpec({

    "Left should throw underlying exception when unwrapped" {
        val a: Either<Exception, Int> = Left(TestErrorOne)

        shouldThrow<TestErrorOne> { a.unwrap() }
    }

    "Side effects should be invoked on proper subtype" {
        val a: Either<Exception, Int> = Left(TestErrorOne)
        val b: Either<Exception, Int> = Right(1)
        var resultA: Either<Unit, Unit>? = null
        var resultB: Either<Unit, Unit>? = null

        with(a) {
            onLeft { resultA = Left(Unit) }
            onRight { resultA = Right(Unit) }
        }
        with(b) {
            onLeft { resultB = Left(Unit) }
            onRight { resultB = Right(Unit) }
        }

        resultA shouldBe Left(Unit)
        resultB shouldBe Right(Unit)
    }
})
