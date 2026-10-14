package ics.model;

import ics.exception.InvalidInputException;
import ics.util.Validate;

import java.time.LocalDate;

/**
 * Reimbursement-style cover: deductible first, then a reimbursement %, then the remaining limit.
 * Worked example (deductible 5,000, rate 80%, limit 100,000):
 *   bill 50,000  -> (50,000 - 5,000) x 0.8 = 36,000
 *   next 100,000 -> 100,000 x 0.8 = 80,000, but only 64,000 limit is left -> 64,000
 */
public class HealthPolicy extends Policy {

    /** PLACEHOLDER demo rate (4% of the coverage limit). Tune it, and say so in the defense. */
    public static final double PREMIUM_RATE = 0.04;

    private double reimbursementRate;   // 0.01 to 1.0 (0.8 = 80%)
    private int waitingPeriodDays;
    private double deductibleMet;       // how much of the deductible is already used up

    public HealthPolicy(String id, String customerId, LocalDate startDate, LocalDate endDate,
                        double deductible, double coverageLimit, double limitUsed, boolean cancelled,
                        double reimbursementRate, int waitingPeriodDays,
                        double deductibleMet) throws InvalidInputException {
        super(id, customerId, startDate, endDate, deductible, coverageLimit, limitUsed, cancelled);
        this.reimbursementRate = Validate.between("Reimbursement rate", reimbursementRate, 0.01, 1.0);
        this.waitingPeriodDays = (int) Validate.nonNegative("Waiting period", waitingPeriodDays);
        this.deductibleMet = Validate.between("Deductible met", deductibleMet, 0, deductible);
    }

    @Override
    public PolicyType getType() {
        return PolicyType.HEALTH;
    }

    @Override
    public double calculatePremium() {
        return roundToCents(getCoverageLimit() * PREMIUM_RATE);
    }

    @Override
    public double calculatePayout(double claimedAmount) {
        double afterDeductible = Math.max(claimedAmount - getRemainingDeductible(), 0);
        double reimbursed = afterDeductible * reimbursementRate;
        return roundToCents(Math.min(reimbursed, getRemainingLimit()));
    }

    @Override
    public void applyPayout(double claimedAmount, double paidAmount) throws InvalidInputException {
        super.applyPayout(claimedAmount, paidAmount);                 // validates first
        deductibleMet += Math.min(claimedAmount, getRemainingDeductible());   // then uses up deductible
    }

    public double getRemainingDeductible() {
        return Math.max(getDeductible() - deductibleMet, 0);
    }

    /** Used by the claim hard rules: true while the waiting period has not finished. */
    public boolean isInWaitingPeriod(LocalDate incidentDate) {
        return incidentDate.isBefore(getStartDate().plusDays(waitingPeriodDays));
    }

    public double getReimbursementRate() { return reimbursementRate; }
    public int getWaitingPeriodDays()    { return waitingPeriodDays; }
    public double getDeductibleMet()     { return deductibleMet; }

    @Override
    protected Object[] extraFields() {
        return new Object[] { reimbursementRate, waitingPeriodDays, deductibleMet };
    }
}
