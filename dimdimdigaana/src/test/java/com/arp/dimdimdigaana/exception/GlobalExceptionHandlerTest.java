package com.arp.dimdimdigaana.exception;

import com.arp.dimdimdigaana.user.exception.UserNotFoundException;
import com.arp.dimdimdigaana.user.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice test for {@link GlobalExceptionHandler}.
 * <p>
 * A minimal stub controller exposes endpoints that deliberately throw each
 * exception type, letting us verify the handler maps them to the correct
 * HTTP status, error code, and response shape — without loading the full
 * application context.
 */
@WebMvcTest
@ContextConfiguration(classes = {
        GlobalExceptionHandlerTest.StubController.class,
        GlobalExceptionHandler.class
})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // ── Stub controller ───────────────────────────────────────────

    @RestController
    @RequestMapping("/test")
    static class StubController {

        @GetMapping("/user-not-found")
        void userNotFound() {
            throw new UserNotFoundException(99L);
        }

        @GetMapping("/username-exists")
        void usernameExists() {
            throw new UsernameAlreadyExistsException("dup_user");
        }

        @GetMapping("/app-exception")
        void appException() {
            throw new AppException(ErrorCode.BAD_REQUEST, "custom bad request");
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new RuntimeException("something went very wrong");
        }

        @GetMapping("/ok")
        String ok() {
            return "ok";
        }
    }

    // ── Tests ─────────────────────────────────────────────────────

    @Test
    @DisplayName("UserNotFoundException → 404 with USER_NOT_FOUND code")
    void userNotFoundException_returns404() throws Exception {
        mockMvc.perform(get("/test/user-not-found").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("USER_NOT_FOUND")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("99")))
                .andExpect(jsonPath("$.path", is("/test/user-not-found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("UsernameAlreadyExistsException → 409 with USERNAME_ALREADY_EXISTS code")
    void usernameAlreadyExistsException_returns409() throws Exception {
        mockMvc.perform(get("/test/username-exists").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("USERNAME_ALREADY_EXISTS")))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("dup_user")));
    }

    @Test
    @DisplayName("AppException with BAD_REQUEST → 400 with custom message")
    void appException_badRequest_returns400() throws Exception {
        mockMvc.perform(get("/test/app-exception").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("custom bad request")));
    }

    @Test
    @DisplayName("Unexpected exception → 500 with INTERNAL_ERROR code, no internal detail leaked")
    void unexpectedException_returns500() throws Exception {
        mockMvc.perform(get("/test/unexpected").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is("INTERNAL_ERROR")))
                .andExpect(jsonPath("$.status", is(500)))
                // internal detail must NOT be exposed to the client
                .andExpect(jsonPath("$.message", not(containsString("something went very wrong"))));
    }

    @Test
    @DisplayName("Unknown path → 404 with NOT_FOUND code")
    void unknownPath_returns404() throws Exception {
        mockMvc.perform(get("/test/does-not-exist").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("Happy path → 200, no error body")
    void happyPath_returns200() throws Exception {
        mockMvc.perform(get("/test/ok"))
                .andExpect(status().isOk());
    }
}

