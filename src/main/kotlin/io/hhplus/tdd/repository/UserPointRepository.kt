package io.hhplus.tdd.repository

import io.hhplus.tdd.domain.UserPoint
import org.springframework.stereotype.Repository

@Repository
interface UserPointRepository {
    fun findById(id: Long): UserPoint

    fun save(userPoint: UserPoint): UserPoint
}