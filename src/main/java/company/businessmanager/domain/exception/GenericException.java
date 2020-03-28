
package company.businessmanager.domain.exception;

public class GenericException extends Exception {

    private final String errorCode;

    public GenericException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

}
