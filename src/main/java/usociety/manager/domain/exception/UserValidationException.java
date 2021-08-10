package usociety.manager.domain.exception;

public class UserValidationException extends GenericException {

    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";

    public UserValidationException(String message) {
        super(message, INVALID_CREDENTIALS);
    }

}
