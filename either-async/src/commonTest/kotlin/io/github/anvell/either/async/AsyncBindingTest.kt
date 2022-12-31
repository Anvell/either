@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.anvell.either.async

import io.github.anvell.either.Either
import io.github.anvell.either.Left
import io.github.anvell.either.Right
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

private object TestErrorOne : Exception()
private object TestErrorTwo : Exception()

class BindingTest {

    @Test
    fun suspendableExpressionsAreProperlyEvaluated() = runTest {
        val a = suspend { Right(1) }
        val b = suspend { Right(2) }

        val result = eitherAsync<TestErrorOne, Int> {
            val one = a().bind()
            val two = b().bind()

            one + two
        }.await()

        assertTrue { result == Right(3) }
    }

    @Test
    fun suspendableAndRegularExpressionsAreProperlyEvaluated() = runTest {
        val a = Right(1)
        val b = suspend { Right(2) }
        val c = suspend { Right(3) }

        val result = eitherAsync<TestErrorOne, Int> {
            val one = a.bind()
            val two = b().bind()
            val three = async { c() }

            one + two + three.bind()
        }.await()

        assertTrue { result == Right(6) }
    }

    @Test
    fun returnsOnFirstLeftExpression() = runTest {
        val a: suspend () -> Either<Exception, Int> = suspend { Right(1) }
        val b: suspend () -> Either<Exception, Int> = suspend { Left(TestErrorOne) }
        val c: suspend () -> Either<Exception, Int> = suspend { Left(TestErrorTwo) }

        val result = eitherAsync {
            val one = async { a() }
            val two = async { b() }
            val three = async { c() }

            one.bind() + two.bind() + three.bind()
        }.await()

        assertTrue { result == Left(TestErrorOne) }
    }

    @Test
    fun returnsOnFirstLeftExpressionWhenDifferentTypesAreUsed() = runTest {
        val a: suspend () -> Either<Exception, Int> = suspend { Right(1) }
        val b: suspend () -> Either<Exception, Float> = suspend { Left(TestErrorOne) }
        val c: Either<Exception, Int> = Left(TestErrorTwo)

        val result = eitherAsync {
            val one = a().bind()
            val two = b().bind()
            val three = c.bind()

            one + two.roundToInt() + three
        }.await()

        assertTrue { result == Left(TestErrorOne) }
    }
}
