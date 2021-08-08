
package usociety.manager.domain.exception;

import static usociety.manager.domain.util.Constants.UNEXPECTED_ERROR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericException extends Exception {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericException.class);

    private final String errorCode;

    public GenericException(String message) {
        super(message);
        this.errorCode = UNEXPECTED_ERROR;
    }

    public GenericException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public GenericException(String message, String errorCode, Throwable throwable) {
        super(message);
        this.errorCode = errorCode;
        LOGGER.error("Generic error", throwable);
    }

    public String getErrorCode() {
        return this.errorCode;
    }

}
