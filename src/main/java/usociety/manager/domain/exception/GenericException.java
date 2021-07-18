
package usociety.manager.domain.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericException extends Exception {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericException.class);

    public static final String GENERIC_ERROR_CODE = "UNEXPECTED_ERROR";
    private final String errorCode;

    public GenericException(String message) {
        super(message);
        this.errorCode = GENERIC_ERROR_CODE;
    }

    public GenericException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public GenericException(String message, String errorCode, Throwable throwable) {
        super(message);
        this.errorCode = errorCode;
        LOGGER.error("Error", throwable);
    }

    public String getErrorCode() {
        return this.errorCode;
    }

}
