package ics.model;

/** Never typed in by anyone: Policy.getStatus() works it out from the dates and the cancelled flag. */
public enum PolicyStatus {
    NOT_STARTED,
    ACTIVE,
    EXPIRED,
    CANCELLED
}
