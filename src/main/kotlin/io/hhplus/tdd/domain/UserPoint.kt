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
}
