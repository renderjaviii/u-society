package usociety.manager.domain.exception;

import static usociety.manager.domain.util.Constants.UNEXPECTED_ERROR;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import usociety.manager.app.api.ApiError;

public class WebException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebException.class);

    private final String errorCode;

    public WebException(String message) {
        super(message);
        this.errorCode = UNEXPECTED_ERROR;
    }

    public WebException(String description, String errorCode) {
        super(description);
        this.errorCode = errorCode;
    }

    public WebException(String message, String errorCode, Throwable throwable) {
        super(message);
        this.errorCode = errorCode;
        LOGGER.error("Web error", throwable);
    }

    public WebException(ApiError error) {
        super(StringUtils.isNotEmpty(error.getDescription()) ? error.getDescription() : error.getErrorDescription());
        this.errorCode = error.getStatusCode();
    }

    public String getErrorCode() {
        return errorCode;
    }

}
