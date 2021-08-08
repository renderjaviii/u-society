package usociety.manager.domain.exception;

import org.apache.commons.lang3.StringUtils;

import usociety.manager.app.api.ApiError;

public class WebException extends RuntimeException {

    public static final String WEB_ERROR = "GENERAL_WEB_ERROR";

    private final String errorCode;

    public WebException(String message) {
        super(message);
        this.errorCode = WEB_ERROR;
    }

    public WebException(String description, String errorCode) {
        super(description);
        this.errorCode = errorCode;
    }

    public WebException(ApiError error) {
        super(StringUtils.isNotEmpty(error.getDescription()) ? error.getDescription() : error.getErrorDescription());
        this.errorCode = error.getStatusCode();
    }

    public String getErrorCode() {
        return errorCode;
    }

}
