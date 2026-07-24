package lv.superchef.app.controller;

import jakarta.persistence.EntityNotFoundException;
import lv.superchef.app.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.ErrorResponse;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("ResponseStatusException with reason: Sets status and explicit reason message")
    void rendersResponseStatusException() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        String view = handler.handleException(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request"),
                response,
                model
        );

        assertThat(view).isEqualTo("error");
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentType()).isEqualTo("text/html");
        assertThat(model.getAttribute("status")).isEqualTo(400);
        assertThat(model.getAttribute("message")).isEqualTo("Invalid request");
    }
    @Test
    @DisplayName("ResponseStatusException without reason: Falls back to Http status reason phrase")
    void rendersResponseStatusException_WithoutReason_FallsBackToReasonPhrase()
    {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        handler.handleException(new ResponseStatusException(HttpStatus.BAD_REQUEST), response, model);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(model.getAttribute("status")).isEqualTo(400);
        assertThat(model.getAttribute("message")).isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("EntityNotFoundException with message: Renders 404 with custom message")
    void rendersMissingEntityAsNotFound() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        handler.handleException(new EntityNotFoundException("Recipe not found: 99"), response, model);

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(model.getAttribute("status")).isEqualTo(404);
        assertThat(model.getAttribute("message")).isEqualTo("Recipe not found: 99");
    }

    @Test
    @DisplayName("EntityNotFoundException without message: Renders 404 with default reason phrase 'Not Found'")
    void rendersMissingEntity_WithoutMessage_FallsBackToReasonPhrase()
    {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        handler.handleException(new EntityNotFoundException(), response, model);

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(model.getAttribute("status")).isEqualTo(404);
        assertThat(model.getAttribute("message")).isEqualTo("Not Found");
    }
    @Test
    @DisplayName("ErrorResponse 4xx with detail: Extracts status code and body detail message")
    void rendersErrorResponse_WithDetail()
    {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        ResponseStatusException ex = new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Invalid payload format"
        );

        handler.handleException(ex, response, model);

        assertThat(response.getStatus()).isEqualTo(422);
        assertThat(model.getAttribute("status")).isEqualTo(422);
        assertThat(model.getAttribute("message")).isEqualTo("Invalid payload format");
    }

    @Test
    @DisplayName("ErrorResponse 4xx without detail: Falls back to HttpStatus reason phrase")
    void rendersErrorResponse_WithoutDetail_FallsBackToReasonPhrase()
    {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        CustomErrorResponseException ex = new CustomErrorResponseException(
                HttpStatus.FORBIDDEN,
                null
        );

        handler.handleException(ex, response, model);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(model.getAttribute("status")).isEqualTo(403);
        assertThat(model.getAttribute("message")).isEqualTo("Forbidden");
    }

    @Test
    @DisplayName("Generic 5xx Exception: Hides sensitive internal details behind standard error message")
    void hidesUnexpectedExceptionDetails() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        ConcurrentModel model = new ConcurrentModel();

        handler.handleException(new IllegalStateException("Sensitive details"), response, model);

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(model.getAttribute("status")).isEqualTo(500);
        assertThat(model.getAttribute("message")).isEqualTo("An unexpected error occurred.");
    }

    private static class CustomErrorResponseException extends Exception implements ErrorResponse
    {
        private final HttpStatusCode status;
        private final String detail;

        public CustomErrorResponseException(HttpStatusCode status, String detail)
        {
            this.status = status;
            this.detail = detail;
        }

        @Override
        public HttpStatusCode getStatusCode()
        {
            return status;
        }

        @Override
        public org.springframework.http.ProblemDetail getBody()
        {
            org.springframework.http.ProblemDetail pd = org.springframework.http.ProblemDetail.forStatus(status);
            pd.setDetail(detail);
            return pd;
        }

        @Override
        public HttpHeaders getHeaders()
        {
            return new HttpHeaders();
        }
    }
}
