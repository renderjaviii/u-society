package common.manager.app.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.common.base.Strings;

import common.manager.app.api.ApiError;
import common.manager.domain.exception.GenericException;
import common.manager.domain.exception.UserValidationException;
import common.manager.domain.exception.WebException;

@ControllerAdvice
@SuppressWarnings("unnused")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String BAD_REQUEST = "BAD_REQUEST";

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        String errorMessage = "Malformed json request: ";
        errorMessage = errorMessage.concat(ex.getCause().getMessage());
        return new ResponseEntity<>(new ApiError(errorMessage, BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        StringJoiner line = new StringJoiner(", ");
        ex.getBindingResult().getAllErrors()
                .forEach(error -> line.add(((FieldError) error).getField() + " " + error.getDefaultMessage()));

        String errorMessage = "Fields validation failed: " + line.toString();
        return new ResponseEntity<>(new ApiError(errorMessage, BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( { ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new LinkedList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        return new ResponseEntity<>(new ApiError(errors.toString(), "CONSTRAINT_VIOLATION_ERROR"),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(HttpServletRequest req,
                                                                 DataIntegrityViolationException e) {

        String errorMessage = "SQL exception: " + e.getCause().getMessage();
        return new ResponseEntity<>(new ApiError(errorMessage, "SQL_INTEGRITY_ERROR"), HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status,
                                                                          WebRequest request) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), "MISSING_REQUEST_PARAMETER"), status);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ApiError> handleGeneric(GenericException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), ex.getErrorCode()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(WebException.class)
    public ResponseEntity<ApiError> handleWeb(WebException ex) {
        String description = Strings.isNullOrEmpty(ex.getMessage()) ? "Request Timeout." : ex.getMessage();
        return new ResponseEntity<>(new ApiError(description, ex.getErrorCode()), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ApiError> handUserValidation(UserValidationException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), ex.getErrorCode()), HttpStatus.FORBIDDEN);
    }

  /*  @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobal(Exception ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), "INTERNAL_SERVER_ERROR"), INTERNAL_SERVER_ERROR);
    }
*/
}
