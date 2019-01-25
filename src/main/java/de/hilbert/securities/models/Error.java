package de.hilbert.securities.models;

/**
 * @author Ralf Hilbert
 * @since 18.01.2019
 */
public class Error implements DataTransferObject {

    private int errorCode;
    private String errorMessage;

    public Error(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
