package io.hhplus.tdd.domain

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    init {
        // id 값이 0 이상이어야 한다.
        require(this.id > 0) { IllegalArgumentException("id is invalid.") }
    }

    // 포인트 충전
    fun charge(amount: Long): UserPoint {
        when {
            amount <= 0 -> throw IllegalArgumentException("point is invalid")
            1000L < this.point + amount -> throw IllegalArgumentException("A request has been made to accumulate more than the maximum balance.")
        }

        return UserPoint(this.id, this.point + amount, System.currentTimeMillis())
    }

    // 포인트 사용
    fun use(amount: Long): UserPoint {

        when {
            amount <= 0 -> throw IllegalArgumentException("amount is invalid")
            this.point < amount -> throw IllegalArgumentException("The accumulated points are less than the points used.")
        }

        return UserPoint(this.id, this.point - amount, System.currentTimeMillis())
    }

    companion object {
        fun create(id: Long): UserPoint {
            return UserPoint(id, 0, System.currentTimeMillis())
        }
    }
}
