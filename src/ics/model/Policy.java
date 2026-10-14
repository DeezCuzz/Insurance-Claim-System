package ics.model;

import ics.exception.InvalidInputException;
import ics.util.FileCodec;
import ics.util.Validate;

import java.time.LocalDate;

/**
 * What every insurance policy has in common. You cannot create a plain Policy:
 * it is always a HealthPolicy or an AutoPolicy (abstraction).
 * The two things that differ per type, premium and payout, are abstract (polymorphism).
 */
public abstract class Policy extends Entity {

    private String customerId;        // link to a Customer by ID, not by object
    private LocalDate startDate;
    private LocalDate endDate;
    private double deductible;
    private double coverageLimit;
    private double limitUsed;         // total already paid out under this policy
    private boolean cancelled;

    protected Policy(String id, String customerId, LocalDate startDate, LocalDate endDate,
                     double deductible, double coverageLimit, double limitUsed,
                     boolean cancelled) throws InvalidInputException {
        super(id);
        this.customerId = Validate.notBlank("Customer", customerId);
        setPeriod(startDate, endDate);
        this.deductible = Validate.nonNegative("Deductible", deductible);
        this.coverageLimit = Validate.positive("Coverage limit", coverageLimit);
        this.limitUsed = Validate.between("Limit used", limitUsed, 0, coverageLimit);
        this.cancelled = cancelled;
    }

    // ---- polymorphic behaviour: each policy type must answer ----
    public abstract PolicyType getType();
    public abstract double calculatePremium();

    /**
     * How much the insurer would pay for this claimed amount, given what this policy has
     * already paid out. Pure calculation: it changes nothing.
     */
    public abstract double calculatePayout(double claimedAmount);

    /** Extra columns that only this policy type has (written after the shared columns). */
    protected abstract Object[] extraFields();

    // ---- shared behaviour ----
    public boolean isActiveOn(LocalDate date) {
        return !cancelled && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /** Active / Expired is worked out from dates, never typed in. */
    public PolicyStatus getStatus(LocalDate today) {
        if (cancelled)                return PolicyStatus.CANCELLED;
        if (today.isBefore(startDate)) return PolicyStatus.NOT_STARTED;
        if (today.isAfter(endDate))    return PolicyStatus.EXPIRED;
        return PolicyStatus.ACTIVE;
    }

    public double getRemainingLimit() {
        return coverageLimit - limitUsed;
    }

    public void cancel() {
        cancelled = true;
    }

    /**
     * Call this AFTER a payout is really made. It uses up part of the coverage limit.
     * HealthPolicy overrides it to also track how much deductible has been met.
     */
    public void applyPayout(double claimedAmount, double paidAmount) throws InvalidInputException {
        Validate.between("Paid amount", paidAmount, 0, getRemainingLimit());
        limitUsed += paidAmount;
    }

    protected static double roundToCents(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ---- getters / setters (data hiding) ----
    public String getCustomerId()   { return customerId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate()   { return endDate; }
    public double getDeductible()   { return deductible; }
    public double getCoverageLimit(){ return coverageLimit; }
    public double getLimitUsed()    { return limitUsed; }
    public boolean isCancelled()    { return cancelled; }

    public final void setPeriod(LocalDate startDate, LocalDate endDate) throws InvalidInputException {
        Validate.notNull("Start date", startDate);
        Validate.notNull("End date", endDate);
        if (!endDate.isAfter(startDate)) {
            throw new InvalidInputException("End date", "End date must be after the start date.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setDeductible(double deductible) throws InvalidInputException {
        this.deductible = Validate.nonNegative("Deductible", deductible);
    }

    public void setCoverageLimit(double coverageLimit) throws InvalidInputException {
        Validate.positive("Coverage limit", coverageLimit);
        if (coverageLimit < limitUsed) {
            throw new InvalidInputException("Coverage limit", "Coverage limit cannot be lower than what was already paid out.");
        }
        this.coverageLimit = coverageLimit;
    }

    @Override
    public String toFileLine() {
        // shared columns first, then the type-specific ones
        Object[] shared = { getId(), getType(), customerId, startDate, endDate,
                            deductible, coverageLimit, limitUsed, cancelled };
        Object[] extra = extraFields();
        Object[] all = new Object[shared.length + extra.length];
        System.arraycopy(shared, 0, all, 0, shared.length);
        System.arraycopy(extra, 0, all, shared.length, extra.length);
        return FileCodec.join(all);
    }
}
