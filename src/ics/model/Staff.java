package ics.model;

import ics.exception.InvalidInputException;

/** Day-to-day user: handles customers, policies and claims, but cannot manage users. */
public class Staff extends User {

    /** Claims with a reserve above this are escalated to an Admin. Keep it a constant, not an if-number. */
    public static final double APPROVAL_LIMIT = 50_000.0;

    public Staff(String id, String username, String passwordHash,
                 int failedAttempts, boolean blocked) throws InvalidInputException {
        super(id, username, passwordHash, failedAttempts, blocked);
    }

    @Override
    public Role getRole() {
        return Role.STAFF;
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public double getApprovalLimit() {
        return APPROVAL_LIMIT;
    }
}
