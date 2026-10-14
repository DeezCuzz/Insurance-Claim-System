package ics.model;

import ics.exception.IllegalStatusTransitionException;
import ics.exception.InvalidInputException;
import ics.util.FileCodec;
import ics.util.Validate;

import java.time.LocalDate;

/**
 * A request to be paid under one policy. It guards its own life cycle:
 * the only way to change status is through methods that ask ClaimStatus.canMoveTo() first,
 * so no screen can skip a step (encapsulation).
 *
 * Four different money fields, never mixed up:
 *   claimedAmount  what the customer asks for
 *   reserveAmount  the insurer's estimate, set when filed
 *   approvedAmount what the payout engine computed at approval
 *   paidAmount     what really went out
 */
public class Claim extends Entity {

    private String policyId;           // link to a Policy by ID
    private LocalDate incidentDate;
    private LocalDate filedDate;
    private String description;
    private double claimedAmount;
    private double reserveAmount;
    private double approvedAmount;
    private double paidAmount;
    private ClaimStatus status;
    private DenialReason denialReason; // only set when REJECTED or DENIED
    private String handledBy;          // ID of the user who decided

    /** Used when loading from a file: every field is given. */
    public Claim(String id, String policyId, LocalDate incidentDate, LocalDate filedDate,
                 String description, double claimedAmount, double reserveAmount,
                 double approvedAmount, double paidAmount, ClaimStatus status,
                 DenialReason denialReason, String handledBy) throws InvalidInputException {
        super(id);
        this.policyId = Validate.notBlank("Policy", policyId);
        this.incidentDate = Validate.notNull("Incident date", incidentDate);
        this.filedDate = Validate.notNull("Filed date", filedDate);
        if (incidentDate.isAfter(filedDate)) {
            throw new InvalidInputException("Incident date", "Incident date cannot be after the filing date.");
        }
        this.description = Validate.notBlank("Description", description);
        this.claimedAmount = Validate.positive("Claimed amount", claimedAmount);
        this.reserveAmount = Validate.nonNegative("Reserve amount", reserveAmount);
        this.approvedAmount = Validate.nonNegative("Approved amount", approvedAmount);
        this.paidAmount = Validate.nonNegative("Paid amount", paidAmount);
        this.status = Validate.notNull("Status", status);
        this.denialReason = denialReason;
        this.handledBy = handledBy;
    }

    /** Used by the File Claim screen: a brand-new claim starts as FILED and has no ID yet. */
    public Claim(String policyId, LocalDate incidentDate, LocalDate filedDate,
                 String description, double claimedAmount) throws InvalidInputException {
        this(null, policyId, incidentDate, filedDate, description, claimedAmount,
             0, 0, 0, ClaimStatus.FILED, null, null);
    }

    // ---- life cycle (every change goes through here) ----
    public void changeStatus(ClaimStatus next) throws IllegalStatusTransitionException {
        if (!status.canMoveTo(next)) {
            throw new IllegalStatusTransitionException(status.name(), next.name());
        }
        status = next;
    }

    /** Automatic hard-rule failure: FILED -> REJECTED. */
    public void reject(DenialReason reason) throws InvalidInputException, IllegalStatusTransitionException {
        Validate.notNull("Denial reason", reason);
        changeStatus(ClaimStatus.REJECTED);
        denialReason = reason;
    }

    /** UNDER_REVIEW -> APPROVED. The amount comes from Policy.calculatePayout(). */
    public void approve(double amount, String userId) throws InvalidInputException, IllegalStatusTransitionException {
        double checked = Validate.positive("Approved amount", amount);
        changeStatus(ClaimStatus.APPROVED);
        approvedAmount = checked;
        handledBy = userId;
    }

    /** UNDER_REVIEW -> DENIED. A reason is compulsory. */
    public void deny(DenialReason reason, String userId) throws InvalidInputException, IllegalStatusTransitionException {
        Validate.notNull("Denial reason", reason);
        changeStatus(ClaimStatus.DENIED);
        denialReason = reason;
        handledBy = userId;
    }

    /** APPROVED -> PAID. A second call fails because PAID cannot move to PAID. */
    public void markPaid(double amount) throws InvalidInputException, IllegalStatusTransitionException {
        double checked = Validate.positive("Paid amount", amount);
        changeStatus(ClaimStatus.PAID);
        paidAmount = checked;
    }

    // ---- getters / setters (data hiding) ----
    public String getPolicyId()          { return policyId; }
    public LocalDate getIncidentDate()   { return incidentDate; }
    public LocalDate getFiledDate()      { return filedDate; }
    public String getDescription()       { return description; }
    public double getClaimedAmount()     { return claimedAmount; }
    public double getReserveAmount()     { return reserveAmount; }
    public double getApprovedAmount()    { return approvedAmount; }
    public double getPaidAmount()        { return paidAmount; }
    public ClaimStatus getStatus()       { return status; }
    public DenialReason getDenialReason(){ return denialReason; }
    public String getHandledBy()         { return handledBy; }

    public void setReserveAmount(double reserveAmount) throws InvalidInputException {
        this.reserveAmount = Validate.nonNegative("Reserve amount", reserveAmount);
    }

    public void setHandledBy(String userId) {
        this.handledBy = userId;
    }

    @Override
    public String toFileLine() {
        return FileCodec.join(getId(), policyId, incidentDate, filedDate, description,
                              claimedAmount, reserveAmount, approvedAmount, paidAmount,
                              status, denialReason, handledBy);
    }
}
