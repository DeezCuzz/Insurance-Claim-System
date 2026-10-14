package ics.model;

import ics.exception.InvalidInputException;
import ics.util.FileCodec;
import ics.util.Validate;

import java.time.LocalDate;

/** The money actually released for one approved claim. Kept separate from the decision on purpose. */
public class Payout extends Entity {

    private String claimId;
    private double amount;
    private LocalDate paidDate;
    private String paidBy;     // ID of the user who released it

    public Payout(String id, String claimId, double amount, LocalDate paidDate,
                  String paidBy) throws InvalidInputException {
        super(id);
        this.claimId = Validate.notBlank("Claim", claimId);
        this.amount = Validate.positive("Amount", amount);
        this.paidDate = Validate.notNull("Paid date", paidDate);
        this.paidBy = Validate.notBlank("Paid by", paidBy);
    }

    public String getClaimId()     { return claimId; }
    public double getAmount()      { return amount; }
    public LocalDate getPaidDate() { return paidDate; }
    public String getPaidBy()      { return paidBy; }

    @Override
    public String toFileLine() {
        return FileCodec.join(getId(), claimId, amount, paidDate, paidBy);
    }
}
