package io.github.anvell.either

import io.github.anvell.either.resources.TestExceptions.TestErrorOne
import io.github.anvell.either.resources.TestExceptions.TestErrorTwo
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.math.roundToInt

class BindingTest : StringSpec({

    "Expressions are properly evaluated" {
        val a = Right(1)
        val b = Right(2)

        val result = either<TestErrorOne, Int> {
            val one = a.bind()
            val two = b.bind()

            one + two
        }

        result shouldBe Right(3)
    }

    "Returns on first left expression" {
        val a: Either<Exception, Int> = Right(1)
        val b: Either<Exception, Int> = Left(TestErrorOne)
        val c: Either<Exception, Int> = Left(TestErrorTwo)

        val result = either<Exception, Int> {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            one + two + three
        }

        result shouldBe Left(TestErrorOne)
    }

    "Returns on first left expression when different types are used" {
        val a: Either<Exception, Int> = Right(1)
        val b: Either<Exception, Float> = Left(TestErrorOne)
        val c: Either<Exception, Int> = Left(TestErrorTwo)

        val result = either<Exception, Int> {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            one + two.roundToInt() + three
        }

        result shouldBe Left(TestErrorOne)
    }
})
