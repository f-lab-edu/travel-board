package com.user.controller;

import com.user.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.user.utils.error.ErrorType.DEFAULT_ERROR;
import static com.user.utils.error.ErrorType.DUPLICATED_EMAIL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalControllerAdviceTest extends ControllerTestSupport {

    @Test
    @DisplayName("CommonException(DUPLICATED_EMAIL)이 발생하면 409 Conflict 응답이 반환되어야 한다")
    void handleCommonExceptionTest() throws Exception {
        // when & then
        mockMvc.perform(get("/test/common-exception")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value(DUPLICATED_EMAIL.getMessage()));
    }

    @Test
    @DisplayName("RuntimeException이 발생하면 500 Internal Server Error 응답이 반환되어야 한다")
    void handleRuntimeExceptionTest() throws Exception {
        // when & then
        mockMvc.perform(get("/test/runtime-exception")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(DEFAULT_ERROR.getMessage()));
    }
}