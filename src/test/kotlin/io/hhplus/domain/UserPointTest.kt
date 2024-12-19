package io.hhplus.domain

import io.hhplus.tdd.domain.UserPoint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.ThrowableAssert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserPointTest {

    @Test
    @DisplayName("0 포인트 충전시 예외 발생")
    fun chargeZero() {
        // given
        val id = 1L
        val point = 0L
        val userPoint = UserPoint(id, point, System.currentTimeMillis())

        // when
        // ThrowableAssert.ThrowingCallable 예외 발생 여부 검증
        val throwingCallable = ThrowableAssert.ThrowingCallable { userPoint.charge(point) }

        // then
        assertThatExceptionOfType(IllegalArgumentException::class.java) // 특정 예외가 발생하는지 검증
            .isThrownBy(throwingCallable) // 정의한 throwingCallable이 실행 중 예외를 던지는지 확인
    }

    @Test
    @DisplayName("음수 포인트 충전시 예외 발생")
    fun chargeNegative() {
        // given
        val id = 1L
        val point = -10L
        val userPoint = UserPoint(id, point, System.currentTimeMillis())

        // when
        val throwingCallable = ThrowableAssert.ThrowingCallable { userPoint.charge(point) }

        // then
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy(throwingCallable)
    }

    @Test
    @DisplayName("포인트 정상 충전")
    fun charge() {
        // given
        val id = 1L
        val point = 0L
        val chargePoint = 10L
        val userPoint = UserPoint(id, point, System.currentTimeMillis())

        // when
        val charge = userPoint.charge(chargePoint)

        // then
        assertThat(charge.point).isEqualTo(10L);
    }

    @Test
    @DisplayName("포인트 정상 사용")
    fun use() {
        // given
        val id = 1L
        val point = 100L
        val usePoint = 50L
        val userPoint = UserPoint(id, point, System.currentTimeMillis())

        // when
        val use = userPoint.use(usePoint)

        // then
        assertThat(use.point).isEqualTo(50L)
    }

    @Test
    @DisplayName("최대 잔고 이상 적립시 예외 발생")
    fun chargeOverPoint() {
        // given
        val id = 1L
        val amount = 100L
        val userPoint = UserPoint(id, 1000L, System.currentTimeMillis())

        // when
        val throwingCallable = ThrowableAssert.ThrowingCallable { userPoint.charge(amount) }

        // then
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy(throwingCallable)
    }

    @Test
    @DisplayName("잔고 부족시 예외 발생")
    fun useOverPoint() {
        // given
        val id = 1L
        val userPoint = UserPoint(id, 50L, System.currentTimeMillis())  // 적립된 포인트는 50
        val useAmount = 100L

        // when
        val throwingCallable = ThrowableAssert.ThrowingCallable { userPoint.use(useAmount) }

        // then
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy(throwingCallable)
    }
}