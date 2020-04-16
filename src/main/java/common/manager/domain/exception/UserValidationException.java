package common.manager.domain.exception;

public class UserValidationException extends GenericException {

    public static final String GENERAL_USER_VALIDATION_ERROR_CODE = "USER_VALIDATION_ERROR";

    public UserValidationException(String message) {
        super(message, GENERAL_USER_VALIDATION_ERROR_CODE);
    }

    public UserValidationException(String message, String errorCode) {
        super(message, errorCode);
    }

}
