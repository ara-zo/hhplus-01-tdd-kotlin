package io.hhplus.tdd.point

import io.hhplus.tdd.domain.PointHistory
import io.hhplus.tdd.domain.TransactionType
import io.hhplus.tdd.domain.UserPoint
import io.hhplus.tdd.repository.PointHistoryRepository
import io.hhplus.tdd.repository.UserPointRepository
import org.springframework.stereotype.Service

@Service
class PointService(
    private var userPointRepository: UserPointRepository,
    private var pointHistoryRepository: PointHistoryRepository,

    // ConcurrentHashMap, ReentrantLock를 이용한 lock 구현
    private val lockService: LockService
){

    // 포인트 조회
    fun findPointById(id: Long): UserPoint {
        return userPointRepository.findById(id)
    }

    // 포인트 충전/사용 내역 조회
    fun findAllPointHistoryById(id: Long): List<PointHistory> {
        return pointHistoryRepository.findAllById(id)
    }

    // 포인트 충전
    fun charge(id: Long, amount: Long): UserPoint {
        return lockService.lock(id) {
            // 1. 포인트 조회
            val userPoint = userPointRepository.findById(id)

            // 2. 포인트 충전
            val result = userPointRepository.save(userPoint.charge(amount))

            // 3. 포인트 충전 내역 등록
            pointHistoryRepository.save(id, TransactionType.CHARGE, amount, System.currentTimeMillis())

            result
        }
    }

    fun use(id: Long, amount: Long): UserPoint {
        return lockService.lock(id) {
            // 1. 포인트 조회
            val userPoint = userPointRepository.findById(id)

            // 2. 포인트 사용
            val result = userPointRepository.save(userPoint.use(amount))

            // 3. 포인트 충전 내역 등록
            pointHistoryRepository.save(id, TransactionType.USE, amount, System.currentTimeMillis())

            result
        }
    }
}