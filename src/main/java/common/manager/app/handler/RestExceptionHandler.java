package common.manager.app.handler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import common.manager.app.api.ApiError;
import common.manager.domain.exception.GenericException;

@ControllerAdvice
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

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<Object> genericExceptionHandler(GenericException ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(HttpServletRequest req,
                                                                     DataIntegrityViolationException e) {
        String errorMessage = "SQL exception: " + e.getCause().getMessage();
        return new ResponseEntity<>(new ApiError(errorMessage, "SQL EXCEPTION"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exceptionHandler(Exception ex) {

        return new ResponseEntity<>(new ApiError(ex.getMessage(), "INTERNAL_SERVER_ERROR"), INTERNAL_SERVER_ERROR);
    }

}
