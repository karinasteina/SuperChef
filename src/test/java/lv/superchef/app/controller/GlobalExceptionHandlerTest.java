package lv.superchef.app.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void rendersResponseStatusException() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        String view = handler.handleException(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request"),
                response,
                model
        );

        assertThat(view).isEqualTo("error-view");
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentType()).isEqualTo("text/html");
        assertThat(model.getAttribute("status")).isEqualTo(400);
        assertThat(model.getAttribute("message")).isEqualTo("Invalid request");
    }

    @Test
    void rendersMissingEntityAsNotFound() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        handler.handleException(new EntityNotFoundException("Recipe not found: 99"), response, model);

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(model.getAttribute("status")).isEqualTo(404);
        assertThat(model.getAttribute("message")).isEqualTo("Recipe not found: 99");
    }

    @Test
    void hidesUnexpectedExceptionDetails() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        handler.handleException(new IllegalStateException("Sensitive details"), response, model);

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(model.getAttribute("status")).isEqualTo(500);
        assertThat(model.getAttribute("message")).isEqualTo("An unexpected error occurred.");
    }
}
