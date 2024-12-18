package io.hhplus.tdd.point

import io.hhplus.tdd.domain.PointHistory
import io.hhplus.tdd.domain.TransactionType
import io.hhplus.tdd.domain.UserPoint
import io.hhplus.tdd.repository.PointHistoryRepository
import io.hhplus.tdd.repository.UserPointRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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

}