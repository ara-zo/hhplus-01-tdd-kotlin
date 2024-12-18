package io.hhplus.tdd.point

import io.hhplus.tdd.domain.UserPoint
import io.hhplus.tdd.repository.UserPointRepository
import org.springframework.stereotype.Service

@Service
class PointService(
    private var userPointRepository: UserPointRepository
){

    // 포인트 조회
    fun findPointById(id: Long): UserPoint {
        return userPointRepository.findById(id)
    }

    // 포인트 충전/사용 내역 조회

    // 포인트 충전

    // 포인트 사용

}