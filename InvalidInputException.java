package ics.exception;

/** A single field failed validation. The GUI can use getFieldName() to highlight it. */
public class InvalidInputException extends ValidationException {

    private static final long serialVersionUID = 1L;

    private final String fieldName;

    public InvalidInputException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
