package io.github.anvell.either

import io.github.anvell.either.binding.either
import io.github.anvell.either.resources.TestExceptions.TestErrorOne
import io.github.anvell.either.resources.TestExceptions.TestErrorTwo
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlin.math.roundToInt

class BindingTest : StringSpec({

    "Expressions are properly evaluated" {
        val a: Either<TestErrorOne, Int> = Right(1)
        val b: Either<TestErrorOne, Int> = Right(2)

        val result = either {
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

        val result = either {
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

        val result = either {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            one + two.roundToInt() + three
        }

        result shouldBe Left(TestErrorOne)
    }

    "Suspendable expressions are properly evaluated" {
        val a = suspend { Right(1) }
        val b = suspend { Right(2) }

        val result = either<TestErrorOne, Int> {
            val one = a().bind()
            val two = b().bind()

            one + two
        }

        result shouldBe Right(3)
    }

    "Suspendable and regular expressions are properly evaluated" {
        val a = Right(1)
        val b = suspend { Right(2) }
        val c = suspend { Right(3) }

        val result = either<TestErrorOne, Int> {
            val one = a.bind()
            val two = b().bind()
            val three = async { c().bind() }

            one + two + three.await()
        }

        result shouldBe Right(6)
    }

    "Returns on first left suspendable expression" {
        val a: suspend () -> Either<Exception, Int> = suspend { Right(1) }
        val b: suspend () -> Either<TestErrorOne, Int> = suspend { Left(TestErrorOne) }
        val c: suspend () -> Either<TestErrorTwo, Int> = suspend { Left(TestErrorTwo) }

        val result = either {
            val one = async { a().bind() }
            val two = async { b().bind() }
            val three = async { c().bind() }

            one.await() + two.await() + three.await()
        }

        result shouldBe Left(TestErrorOne)
    }

    "Returns on first left suspendable expression when different types are used" {
        val a: suspend () -> Either<Exception, Int> = suspend { Right(1) }
        val b: suspend () -> Either<Exception, Float> = suspend { Left(TestErrorOne) }
        val c: Either<Exception, Int> = Left(TestErrorTwo)

        val result = either {
            val one = a().bind()
            val two = b().bind()
            val three = c.bind()

            one + two.roundToInt() + three
        }

        result shouldBe Left(TestErrorOne)
    }
})
