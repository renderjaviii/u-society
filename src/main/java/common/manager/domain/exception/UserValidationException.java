package common.manager.domain.exception;

public class UserValidationException extends GenericException {

    public static final String GENERAL_USER_VALIDATION_ERROR = "USER_VALIDATION_ERROR";
    public static final String LOGIN_ERROR = "USER_LOGIN_ERROR";

    public UserValidationException(String message) {
        super(message, GENERAL_USER_VALIDATION_ERROR);
    }

    public UserValidationException(String message, String errorCode) {
        super(message, errorCode);
    }

}
