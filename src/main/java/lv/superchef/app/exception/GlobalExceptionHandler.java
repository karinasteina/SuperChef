package lv.superchef.app.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception, HttpServletResponse response, Model model) {
        HttpStatusCode status = resolveStatus(exception);
        String message = resolveMessage(exception, status);


        logger.error("Request failed with status {} {}", status.value(), exception.getMessage());

        response.setStatus(status.value());
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        model.addAttribute("status", status.value());
        model.addAttribute("message", message);

        return "error";
    }

    private HttpStatusCode resolveStatus(Exception exception) {
        if (exception instanceof EntityNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }

        if (exception instanceof ErrorResponse errorResponse) {
            return errorResponse.getStatusCode();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveMessage(Exception exception, HttpStatusCode status) {
        if (exception instanceof ResponseStatusException responseStatusException
                && StringUtils.hasText(responseStatusException.getReason())) {
            return responseStatusException.getReason();
        }

        if (exception instanceof EntityNotFoundException && StringUtils.hasText(exception.getMessage())) {
            return exception.getMessage();
        }

        if (exception instanceof ErrorResponse errorResponse
                && StringUtils.hasText(errorResponse.getBody().getDetail())) {
            return errorResponse.getBody().getDetail();
        }

        if (status.is5xxServerError()) {
            return "An unexpected error occurred.";
        }

        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        return httpStatus != null ? httpStatus.getReasonPhrase() : "Request failed.";
    }
}
