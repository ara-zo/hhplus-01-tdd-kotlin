package io.hhplus.tdd.repository

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.domain.UserPoint
import org.springframework.stereotype.Component

@Component
class UserPointRepositoryImpl(
    private val userPointTable: UserPointTable
) : UserPointRepository {
    override fun findById(id: Long): UserPoint {
        if (id <= 0) {
            throw IllegalArgumentException("id is invalid.")
        }
        return userPointTable.selectById(id)
    }

    override fun save(userPoint: UserPoint): UserPoint {
        if (userPoint.id <= 0) {
            throw IllegalArgumentException("id is invalid.")
        }
        return userPointTable.insertOrUpdate(id = userPoint.id, amount = userPoint.point)
    }
}