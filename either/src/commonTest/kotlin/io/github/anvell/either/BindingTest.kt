package io.github.anvell.either

import io.github.anvell.either.resources.TestExceptions.TestErrorOne
import io.github.anvell.either.resources.TestExceptions.TestErrorTwo
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

class BindingTest {

    @Test
    fun expressionsAreProperlyEvaluated() {
        val a = Right(1)
        val b = Right(2)

        val result = either<TestErrorOne, Int> {
            val one = a.bind()
            val two = b.bind()

            one + two
        }

        assertTrue { result == Right(3) }
    }

    @Test
    fun returnsOnFirstLeftExpression() {
        val a: Either<Exception, Int> = Right(1)
        val b: Either<Exception, Int> = Left(TestErrorOne)
        val c: Either<Exception, Int> = Left(TestErrorTwo)

        val result = either {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            one + two + three
        }

        assertTrue { result == Left(TestErrorOne) }
    }

    @Test
    fun returnsOnFirstLeftExpressionWhenDifferentTypesAreUsed() {
        val a: Either<Exception, Int> = Right(1)
        val b: Either<Exception, Float> = Left(TestErrorOne)
        val c: Either<Exception, Int> = Left(TestErrorTwo)

        val result = either {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            one + two.roundToInt() + three
        }

        assertTrue { result == Left(TestErrorOne) }
    }
}
