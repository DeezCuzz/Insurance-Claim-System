package ics.exception;

/**
 * Root of every custom exception in the Insurance Claims System.
 * Checked on purpose: the compiler forces each caller to handle or declare it.
 *
 * Keep this package free of imports from ics.model so the two packages
 * never depend on each other (that is why subclasses carry Strings, not enums).
 */
public class ICSException extends Exception {

    private static final long serialVersionUID = 1L;

    public ICSException(String message) {
        super(message);
    }

    public ICSException(String message, Throwable cause) {
        super(message, cause);
    }
}
