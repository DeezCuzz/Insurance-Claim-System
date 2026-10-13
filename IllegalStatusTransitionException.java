package ics.exception;

/** Thrown when code tries to skip a step in the claim life cycle (e.g. FILED -> PAID). */
public class IllegalStatusTransitionException extends ICSException {

    private static final long serialVersionUID = 1L;

    private final String from;
    private final String to;

    public IllegalStatusTransitionException(String from, String to) {
        super("A claim cannot move from " + from + " to " + to + ".");
        this.from = from;
        this.to = to;
    }

    public String getFrom() { return from; }
    public String getTo()   { return to; }
}
