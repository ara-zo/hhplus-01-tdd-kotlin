package io.hhplus.tdd.point

import io.hhplus.tdd.IntegratedTest
import io.hhplus.tdd.domain.PointHistory
import io.hhplus.tdd.domain.UserPoint
import io.restassured.RestAssured
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import java.util.concurrent.CompletableFuture

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PointControllerIntegratedTest : IntegratedTest() {
    @Autowired
    lateinit var pointService: PointService

    private val PATH: String = "/point"

    @Test
    @DisplayName("포인트 조회을 조회한다.")
    fun point() {
        // given
        val id = 1L

        // when
        val result: ExtractableResponse<Response> = RestAssured
            .given().log().all()
            .`when`().get("$PATH/$id")
            .then().log().all().extract()

        // then
        assertEquals(HttpStatus.OK.value(), result.statusCode())
    }

    @Test
    @DisplayName("포인트 충전/이용 내역을 조회한다.")
    fun history() {
        // given
        val id = 1L

        pointService.charge(id, 20L)
        pointService.use(id, 10L)
        pointService.charge(id, 10L)

        // when
        val result: ExtractableResponse<Response> = RestAssured
            .given().log().all()
            .`when`().get("$PATH/$id/histories")
            .then().log().all().extract()

        // then
        val pointHistoryList: List<PointHistory> = pointService.findAllPointHistoryById(id)
        assertEquals(HttpStatus.OK.value(), result.statusCode())
        assertThat(pointHistoryList).hasSize(3);
    }

    @Test
    @DisplayName("동시에 10포인트씩 10번 충전한다.")
    fun charge() {
        // given
        val id = 1L
        val amount = 10L

        // when
        val cnt = 10
        val futureArray = Array(cnt) {
            CompletableFuture.runAsync {
                RestAssured
                    .given().log().all()
                    .body(amount)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .`when`().patch("$PATH/$id/charge")
                    .then().log().all().extract()
            }
        }
        CompletableFuture.allOf(*futureArray).join()

        // then
        val userPoint: UserPoint = pointService.findPointById(id)
        assertEquals(amount * 10, userPoint.point)
    }

    @Test
    @DisplayName("동시에 10포인트씩 10번 사용한다.")
    fun use() {
        // given
        val id = 1L
        val amount = 10L
        // 100 포인트 충전
        pointService.charge(id, 100L)

        // when
        val cnt = 10
        val futureArray = Array(cnt) {
            CompletableFuture.runAsync {
                RestAssured
                    .given().log().all()
                    .body(amount)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .`when`().patch("$PATH/$id/use")
                    .then().log().all().extract()
            }
        }
        CompletableFuture.allOf(*futureArray).join()

        // then
        val userPoint: UserPoint = pointService.findPointById(id)
        assertEquals(0L, userPoint.point)
    }

    @Test
    @DisplayName("동시성 테스트 - 동시에 포인트 충전/차감 한다.")
    fun concurrencyPointChargeAndUse2() {
        // given
        val id = 1L
        val 보유포인트 = 100L
        val 사용포인트 = 10L
        val 충전포인트 = 10L
        val 사용포인트2 = 10L
        pointService.charge(id, 보유포인트)

        // when
        CompletableFuture.allOf(
            CompletableFuture.runAsync {
                RestAssured
                    .given().log().all()
                    .body(사용포인트)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .`when`().patch("$PATH/$id/use")
                    .then().log().all().extract()
            },
            CompletableFuture.runAsync {
                RestAssured
                    .given().log().all()
                    .body(충전포인트)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .`when`().patch("$PATH/$id/charge")
                    .then().log().all().extract()
            },
            CompletableFuture.runAsync {
                RestAssured
                    .given().log().all()
                    .body(사용포인트2)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .`when`().patch("$PATH/$id/use")
                    .then().log().all().extract()
            }
        ).join()

        // then
        val userPoint: UserPoint = pointService.findPointById(id)
        assertEquals(보유포인트 - 사용포인트 + 충전포인트 - 사용포인트2, userPoint.point)
    }
}