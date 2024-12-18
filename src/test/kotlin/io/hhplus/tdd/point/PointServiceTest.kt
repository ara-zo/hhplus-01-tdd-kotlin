package io.hhplus.tdd.point

import io.hhplus.tdd.domain.UserPoint
import io.hhplus.tdd.repository.UserPointRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceTest {

    @Mock
    private lateinit var pointRepository: UserPointRepository

    @InjectMocks
    private lateinit var pointService: PointService

    @Test
    @DisplayName("아이디로 포인트 조회")
    fun findPointById() {
        // given
        val id = 1L
        given(pointRepository.findById(id)).willReturn(UserPoint(id, 0, System.currentTimeMillis()))

        // when
        val result = pointService.findPointById(id)

        // then
        assertEquals(id, result.id)
        assertEquals(0, result.point)
    }
}