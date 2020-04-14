package common.manager.domain.exception;

public class WebException extends GenericException {

    public static final String GENERAL_WEB_ERROR_CODE = "GENERAL_WEB_ERROR";

    public WebException(String message) {
        super(message, GENERAL_WEB_ERROR_CODE);
    }

    public WebException(String description, String errorCode) {
        super(description, errorCode);
    }

}
