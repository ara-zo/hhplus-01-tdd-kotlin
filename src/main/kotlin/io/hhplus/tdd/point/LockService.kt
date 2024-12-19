package io.hhplus.tdd.point

import io.hhplus.tdd.Exception.LockException
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier

@Service
class LockService {
    private val lockMap = ConcurrentHashMap<Long, ReentrantLock>()

    fun <T> lock(id: Long, supplier: Supplier<T>): T {
        val lock = lockMap.computeIfAbsent(id) { k: Long? -> ReentrantLock(true) }
        val isLock: Boolean
        try {
            isLock = lock.tryLock(1, TimeUnit.MINUTES)
            if (!isLock) {
                throw LockException("lock acquisition failed")
            }
            return supplier.get()
        } catch (e: InterruptedException) {
            throw LockException(e.message)
        } finally {
            lock.unlock()
        }
    }
}