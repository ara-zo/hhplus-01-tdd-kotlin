package io.hhplus.tdd.point

import io.hhplus.tdd.domain.UserPoint
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class PointControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc // MockMvc를 주입받기 위한 변수

    private val PATH: String = "/point"

    val initialPoint = 1000L // 초기 포인트 설정

    @BeforeEach
    fun setUp() {
        UserPoint(1, initialPoint, System.currentTimeMillis())
    }

    @Test
    @DisplayName("포인트 조회을 조회한다.")
    fun point() {
        // given
        val id = 1L

        // when
        val result = mockMvc.perform(get("$PATH/$id"))

        // then
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
    }

    @Test
    @DisplayName("포인트 충전/이용 내역을 조회한다.")
    fun history() {
        // given
        val id = 1L

        // when
        val result = mockMvc.perform(get("$PATH/$id/histories"))

        // then
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    @DisplayName("포인트를 충전한다.")
    fun charge() {
        // given
        val id = 1L
        val amount = 1000L

        // when
        val result = mockMvc.perform(
            patch("$PATH/$id/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(amount.toString())
        )

        // then
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.point").value(amount))
    }

    @Test
    @DisplayName("포인트를 사용한다.")
    fun use() {
        // given
        val id = 1L
        val amount = 100L
        val expectedPoint = initialPoint - amount // 사용 후 예상 포인트

        // when
        val result = mockMvc.perform(
            patch("$PATH/$id/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(amount.toString())
        )

        // then
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.point").value(expectedPoint))
    }
}