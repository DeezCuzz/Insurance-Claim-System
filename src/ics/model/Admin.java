package ics.model;

import ics.exception.InvalidInputException;

/** Full access: manages users, unblocks accounts, confirms deletes, approves any amount. */
public class Admin extends User {

    public Admin(String id, String username, String passwordHash,
                 int failedAttempts, boolean blocked) throws InvalidInputException {
        super(id, username, passwordHash, failedAttempts, blocked);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }

    @Override
    public double getApprovalLimit() {
        return Double.MAX_VALUE;
    }
}
