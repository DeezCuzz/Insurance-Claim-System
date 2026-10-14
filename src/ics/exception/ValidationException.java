package ics.exception;

/** Something the user typed (or a loaded value) is not acceptable. */
public class ValidationException extends ICSException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
