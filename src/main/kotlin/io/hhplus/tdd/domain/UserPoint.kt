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
    fun charge(point: Long): UserPoint {
        if (point <= 0) {
            throw IllegalArgumentException("point is invalid")
        }
        return UserPoint(this.id, this.point + point, System.currentTimeMillis())
    }
}
