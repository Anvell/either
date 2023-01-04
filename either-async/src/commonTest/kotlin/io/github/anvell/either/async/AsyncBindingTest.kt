@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.anvell.either.async

import io.github.anvell.either.Either
import io.github.anvell.either.Left
import io.github.anvell.either.Right
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlin.math.roundToInt

private object TestErrorOne : Exception()
private object TestErrorTwo : Exception()

class AsyncBindingTest : StringSpec({

    "Suspendable expressions are properly evaluated" {
        runTest {
            val a = suspend { Right(1) }
            val b = suspend { Right(2) }

            val result = eitherAsync<TestErrorOne, Int> {
                val one = a().bind()
                val two = b().bind()

                one + two
            }.await()

            result shouldBe Right(3)
        }
    }

    "Suspendable and regular expressions are properly evaluated" {
        runTest {
            val a = Right(1)
            val b = suspend { Right(2) }
            val c = suspend { Right(3) }

            val result = eitherAsync<TestErrorOne, Int> {
                val one = a.bind()
                val two = b().bind()
                val three = async { c() }

                one + two + three.bind()
            }.await()

            result shouldBe Right(6)
        }
    }

    "Returns on first left expression" {
        runTest {
            val a: suspend () -> Either<Exception, Int> = suspend { Right(1) }
            val b: suspend () -> Either<Exception, Int> = suspend { Left(TestErrorOne) }
            val c: suspend () -> Either<Exception, Int> = suspend { Left(TestErrorTwo) }

            val result = eitherAsync {
                val one = async { a() }
                val two = async { b() }
                val three = async { c() }

                one.bind() + two.bind() + three.bind()
            }.await()

            result shouldBe Left(TestErrorOne)
        }
    }

    "Returns on first left expression when different types are used" {
        runTest {
            val a: suspend () -> Either<Exception, Int> = suspend { Right(1) }
            val b: suspend () -> Either<Exception, Float> = suspend { Left(TestErrorOne) }
            val c: Either<Exception, Int> = Left(TestErrorTwo)

            val result = eitherAsync {
                val one = a().bind()
                val two = b().bind()
                val three = c.bind()

                one + two.roundToInt() + three
            }.await()

            result shouldBe Left(TestErrorOne)
        }
    }
})
