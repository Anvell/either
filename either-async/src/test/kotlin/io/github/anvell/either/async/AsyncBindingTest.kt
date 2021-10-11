package io.github.anvell.either.async

import io.github.anvell.either.Either
import io.github.anvell.either.Left
import io.github.anvell.either.Right
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.math.roundToInt

private object TestErrorOne : Exception()
private object TestErrorTwo : Exception()

class BindingTest {

    @Test
    fun `Suspendable expressions are properly evaluated`() {
        val a = suspend { Right(1) }
        val b = suspend { Right(2) }

        runBlocking {
            eitherAsync<TestErrorOne, Int> {
                val one = a().bind()
                val two = b().bind()

                one + two
            }.await() shouldBe Right(3)
        }
    }

    @Test
    fun `Suspendable and regular expressions are properly evaluated`() {
        val a = Right(1)
        val b = suspend { Right(2) }
        val c = suspend { Right(3) }

        runBlocking {
            eitherAsync<TestErrorOne, Int> {
                val one = a.bind()
                val two = b().bind()
                val three = async { c() }

                one + two + three.bind()
            }.await() shouldBe Right(6)
        }
    }

    @Test
    fun `Returns on first left expression`() {
        val a: suspend () -> Either<Exception, Int> = suspend { Right(1) }
        val b: suspend () -> Either<Exception, Int> = suspend { Left(TestErrorOne) }
        val c: suspend () -> Either<Exception, Int> = suspend { Left(TestErrorTwo) }

        runBlocking {
            eitherAsync<Exception, Int> {
                val one = async { a() }
                val two = async { b() }
                val three = async { c() }

                one.bind() + two.bind() + three.bind()
            }.await() shouldBe Left(TestErrorOne)
        }
    }

    @Test
    fun `Returns on first left expression when different types are used`() {
        val a: suspend () -> Either<Exception, Int> = suspend { Right(1) }
        val b: suspend () -> Either<Exception, Float> = suspend { Left(TestErrorOne) }
        val c: Either<Exception, Int> = Left(TestErrorTwo)

        runBlocking {
            eitherAsync<Exception, Int> {
                val one = a().bind()
                val two = b().bind()
                val three = c.bind()

                one + two.roundToInt() + three
            }.await() shouldBe Left(TestErrorOne)
        }
    }
}
