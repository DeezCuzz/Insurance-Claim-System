package ics.model;

/**
 * Every stage of a claim, plus the only legal moves between them.
 * Claim.changeStatus() asks canMoveTo() before it changes anything.
 *
 *   FILED -> UNDER_REVIEW | REJECTED
 *   UNDER_REVIEW -> APPROVED | DENIED | INFO_REQUESTED
 *   INFO_REQUESTED -> UNDER_REVIEW
 *   APPROVED -> PAID
 *   DENIED, PAID -> CLOSED
 *   REJECTED, CLOSED -> (end)
 */
public enum ClaimStatus {
    FILED,
    REJECTED,
    UNDER_REVIEW,
    INFO_REQUESTED,
    APPROVED,
    DENIED,
    PAID,
    CLOSED;

    public boolean canMoveTo(ClaimStatus next) {
        return switch (this) {
            case FILED          -> next == UNDER_REVIEW || next == REJECTED;
            case UNDER_REVIEW   -> next == APPROVED || next == DENIED || next == INFO_REQUESTED;
            case INFO_REQUESTED -> next == UNDER_REVIEW;
            case APPROVED       -> next == PAID;
            case DENIED, PAID   -> next == CLOSED;
            default             -> false;
        };
    }
}
