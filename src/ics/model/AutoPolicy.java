package ics.model;

import ics.exception.InvalidInputException;
import ics.util.Validate;

import java.time.LocalDate;

/**
 * Vehicle cover: the deductible comes off every claim, then the result is capped at the remaining limit.
 * Worked example (deductible 10,000, limit 300,000):
 *   estimate 120,000 -> 120,000 - 10,000 = 110,000
 *   estimate 350,000 -> 340,000, capped at the limit -> 300,000
 */
public class AutoPolicy extends Policy {

    /** PLACEHOLDER demo rate (3% of the vehicle value). Tune it, and say so in the defense. */
    public static final double PREMIUM_RATE = 0.03;

    private double vehicleValue;
    private String plateNo;

    public AutoPolicy(String id, String customerId, LocalDate startDate, LocalDate endDate,
                      double deductible, double coverageLimit, double limitUsed, boolean cancelled,
                      double vehicleValue, String plateNo) throws InvalidInputException {
        super(id, customerId, startDate, endDate, deductible, coverageLimit, limitUsed, cancelled);
        this.vehicleValue = Validate.positive("Vehicle value", vehicleValue);
        this.plateNo = Validate.notBlank("Plate number", plateNo);
    }

    @Override
    public PolicyType getType() {
        return PolicyType.AUTO;
    }

    @Override
    public double calculatePremium() {
        return roundToCents(vehicleValue * PREMIUM_RATE);
    }

    @Override
    public double calculatePayout(double claimedAmount) {
        double afterDeductible = Math.max(claimedAmount - getDeductible(), 0);
        return roundToCents(Math.min(afterDeductible, getRemainingLimit()));
    }

    public double getVehicleValue() { return vehicleValue; }
    public String getPlateNo()      { return plateNo; }

    @Override
    protected Object[] extraFields() {
        return new Object[] { vehicleValue, plateNo };
    }
}
