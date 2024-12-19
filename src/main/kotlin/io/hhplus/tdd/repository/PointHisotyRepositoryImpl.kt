package io.hhplus.tdd.repository

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.domain.PointHistory
import io.hhplus.tdd.domain.TransactionType
import org.springframework.stereotype.Component

@Component
class PointHistoryRepositoryImpl(
    private val pointHistoryTable: PointHistoryTable
) : PointHistoryRepository {
    override fun findAllById(id: Long): List<PointHistory> {
        if (id <= 0) {
            throw IllegalArgumentException("id is invalid.")
        }

        return pointHistoryTable.selectAllByUserId(id)
    }

    override fun save(id: Long, type: TransactionType, amount: Long, timeMillis: Long): PointHistory {
        if (id <= 0) {
            throw IllegalArgumentException("id is invalid.")
        }
        return pointHistoryTable.insert(id = id, transactionType = type, amount = amount, updateMillis = System.currentTimeMillis())
    }
}