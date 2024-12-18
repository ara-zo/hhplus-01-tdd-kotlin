package io.hhplus.tdd.repository

import io.hhplus.tdd.domain.PointHistory
import io.hhplus.tdd.domain.TransactionType
import org.springframework.stereotype.Repository

@Repository
interface PointHistoryRepository {
    fun findAllById(id: Long): List<PointHistory>

    fun save(id: Long, type: TransactionType, amount: Long, timeMillis: Long): PointHistory
}