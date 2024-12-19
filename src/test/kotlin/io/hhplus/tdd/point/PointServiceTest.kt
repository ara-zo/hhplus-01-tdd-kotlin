package io.hhplus.tdd.point

import io.hhplus.tdd.any
import io.hhplus.tdd.domain.PointHistory
import io.hhplus.tdd.domain.TransactionType
import io.hhplus.tdd.domain.UserPoint
import io.hhplus.tdd.repository.PointHistoryRepository
import io.hhplus.tdd.repository.UserPointRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.`when`
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceTest {

    @Mock
    private lateinit var userPointRepository: UserPointRepository

    @Mock
    private lateinit var pointHistoryRepository: PointHistoryRepository

    @InjectMocks
    private lateinit var pointService: PointService

    @Test
    @DisplayName("아이디로 포인트 조회")
    fun findPointById() {
        // given
        val id = 1L
        given(userPointRepository.findById(id)).willReturn(UserPoint(id, 0, System.currentTimeMillis()))

        // when
        val result = pointService.findPointById(id)

        // then
        assertThat(result.id).isEqualTo(id)
        assertThat(result.point).isZero()
    }

    @Test
    @DisplayName("포인트 충전/이용 내역을 조회한다.")
    fun history() {
        // given
        val id = 1L

        // when
        val pointHistoryList = listOf(
            PointHistory(1L, id, TransactionType.CHARGE, 100L, System.currentTimeMillis()),
            PointHistory(2L, id, TransactionType.USE, 10L, System.currentTimeMillis()),
            PointHistory(3L, id, TransactionType.USE, 10L, System.currentTimeMillis())
        )
        `when`(pointHistoryRepository.findAllById(id)).thenReturn(pointHistoryList)

        val result = pointHistoryRepository.findAllById(id)

        // then
        assertThat(result).hasSize(3)
        assertThat(result.map { it.id }).containsExactly(1L, 2L, 3L)
        assertThat(result[0].amount).isEqualTo(100L)
    }


    @Test
    @DisplayName("음수값으로 포인트 충전시 예외 발생")
    fun chargeNegative() {
        // given
        val id = 1L
        val amount = -100L

        // when
        `when`(userPointRepository.findById(id)).thenReturn(UserPoint(id, amount, System.currentTimeMillis()))

        // then
        assertThrows(IllegalArgumentException::class.java) { pointService.charge(id, amount) }
    }

    @Test
    @DisplayName("정상적인 값으로 포인트 충전")
    fun charge() {
        // given
        val id = 1L
        val amount = 100L
        val userPoint = UserPoint.create(id)

        // when
        `when`(userPointRepository.findById(id)).thenReturn(userPoint)
        `when`(userPointRepository.save(any(UserPoint::class.java)))
            .thenReturn(UserPoint(id, userPoint.point + amount, anyLong()))

        val result = pointService.charge(id, amount)

        // then
        assertThat(result.point).isEqualTo(userPoint.point + amount)
    }

    @Test
    @DisplayName("음수값으로 포인트 사용시 예외 발생")
    fun useNegative() {
        // given
        val id = 1L
        val amount = -100L

        // when
        `when`(userPointRepository.findById(id)).thenReturn(UserPoint(id, amount, System.currentTimeMillis()))

        // then
        assertThrows(IllegalArgumentException::class.java) { pointService.use(id, amount) }
    }

    @Test
    @DisplayName("적립된 포인트보다 많이 사용 시 예외 발생 (잔고 부족)")
    fun useOverPoint() {
        // given
        val id = 1L
        val userPoint = UserPoint(id, 50L, System.currentTimeMillis())  // 적립된 포인트는 50
        val useAmount = 100L

        // when
        `when`(userPointRepository.findById(id)).thenReturn(userPoint)

        // then
        assertThatThrownBy { pointService.use(id, useAmount) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("The accumulated points are less than the points used.")
    }

    @Test
    @DisplayName("최대 잔고 이상 적립시 예외 발생")
    fun chargeOverPoint() {
        // given
        val id = 1L
        val amount = 100L
        val userPoint = UserPoint(id, 1000L, System.currentTimeMillis())

        // when
        `when`(userPointRepository.findById(id)).thenReturn(userPoint)

        // then
        assertThatThrownBy { pointService.charge(id, amount) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("A request has been made to accumulate more than the maximum balance.")
    }
}