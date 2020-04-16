package common.manager.domain.exception;

public class WebException extends GenericException {

    public static final String WEB_ERROR = "GENERAL_WEB_ERROR";

    public WebException(String message) {
        super(message, WEB_ERROR);
    }

    public WebException(String description, String errorCode) {
        super(description, errorCode);
    }

}
