package ics.model;

/**
 * Reason codes for a claim that cannot be paid.
 * The first seven come from the automatic hard rules; the last three are chosen by a reviewer.
 */
public enum DenialReason {
    POLICY_INACTIVE("Policy is not active"),
    OUTSIDE_COVERAGE_PERIOD("Incident is outside the coverage period"),
    WAITING_PERIOD("Incident happened inside the waiting period"),
    LATE_FILING("Claim was filed too late"),
    DUPLICATE_CLAIM("Same incident was already claimed"),
    LIMIT_EXHAUSTED("Coverage limit is used up"),
    INVALID_AMOUNT("Claimed amount is not valid"),
    EXCLUDED_CAUSE("Cause of loss is not covered"),
    SUSPECTED_FRAUD("Claim failed the fraud review"),
    INSUFFICIENT_DOCUMENTS("Supporting documents are missing");

    private final String label;

    DenialReason(String label) {
        this.label = label;
    }

    /** Friendly text for screens and the payout slip. */
    public String getLabel() {
        return label;
    }
}
