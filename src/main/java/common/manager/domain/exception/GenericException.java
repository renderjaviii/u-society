
package common.manager.domain.exception;

public class GenericException extends Exception {

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

    public String getErrorCode() {
        return this.errorCode;
    }

}
