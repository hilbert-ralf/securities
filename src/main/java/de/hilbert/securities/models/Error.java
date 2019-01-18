package de.hilbert.securities.models;

/**
 * @author Ralf Hilbert
 * @since 18.01.2019
 */
public class Error implements DataTransferObject {

    private String message;

    public Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
