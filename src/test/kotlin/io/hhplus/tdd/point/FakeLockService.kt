package io.hhplus.tdd.point

class FakeLockService : LockService() {
    fun <T> lock(id:Long, action: () -> T): () -> T {
        return action
    }
}