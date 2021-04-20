package io.github.anvell.either

import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.math.roundToInt

private object TestErrorOne : Exception()
private object TestErrorTwo : Exception()

class BindingTest {

    @Test
    fun `Expressions are properly evaluated`() {
        val a = Right(1)
        val b = Right(2)

        either<TestErrorOne, Int> {
            val one = a.bind()
            val two = b.bind()

            one + two
        } shouldBe Right(3)
    }

    @Test
    fun `Returns on first left expression`() {
        val a: Either<Exception, Int> = Right(1)
        val b: Either<Exception, Int> = Left(TestErrorOne)
        val c: Either<Exception, Int> = Left(TestErrorTwo)

        either<Exception, Int> {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            one + two + three
        } shouldBe Left(TestErrorOne)
    }

    @Test
    fun `Returns on first left expression when different types are used`() {
        val a: Either<Exception, Int> = Right(1)
        val b: Either<Exception, Float> = Left(TestErrorOne)
        val c: Either<Exception, Int> = Left(TestErrorTwo)

        either<Exception, Int> {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            one + two.roundToInt() + three
        } shouldBe Left(TestErrorOne)
    }
}
