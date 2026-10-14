package ics.model;

import ics.exception.InvalidInputException;
import ics.util.FileCodec;
import ics.util.Validate;

/**
 * A person who can log in. Customers are NOT users (they are plain records).
 * Admin and Staff differ only in what they are allowed to do, so those answers are abstract.
 * Screens ask  user.canManageUsers()  instead of checking a role string.
 */
public abstract class User extends Entity {

    public static final int MAX_FAILED_ATTEMPTS = 3;

    private String username;
    private String passwordHash;     // SHA-256 hex, see PasswordUtil
    private int failedAttempts;
    private boolean blocked;

    protected User(String id, String username, String passwordHash,
                   int failedAttempts, boolean blocked) throws InvalidInputException {
        super(id);
        setUsername(username);
        setPasswordHash(passwordHash);
        this.failedAttempts = (int) Validate.nonNegative("Failed attempts", failedAttempts);
        this.blocked = blocked;
    }

    // ---- polymorphic behaviour: each subclass must answer ----
    public abstract Role getRole();
    public abstract boolean canManageUsers();
    public abstract double getApprovalLimit();

    // ---- login helpers ----
    /** The caller passes the HASH of what the user typed (use PasswordUtil.hash). */
    public boolean verifyPassword(String attemptHash) {
        return passwordHash.equals(attemptHash);
    }

    /** Third wrong password blocks the account (rubric II.C.6). */
    public void recordFailedAttempt() {
        failedAttempts++;
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            blocked = true;
        }
    }

    public void resetFailedAttempts() {
        failedAttempts = 0;
    }

    /** Only an Admin should call this (the service layer checks). */
    public void unblock() {
        blocked = false;
        failedAttempts = 0;
    }

    // ---- getters / setters (data hiding) ----
    public String getUsername()     { return username; }
    public String getPasswordHash() { return passwordHash; }
    public int getFailedAttempts()  { return failedAttempts; }
    public boolean isBlocked()      { return blocked; }

    public final void setUsername(String username) throws InvalidInputException {
        this.username = Validate.notBlank("Username", username);
    }

    public final void setPasswordHash(String passwordHash) throws InvalidInputException {
        this.passwordHash = Validate.notBlank("Password", passwordHash);
    }

    @Override
    public String toFileLine() {
        return FileCodec.join(getId(), getRole(), username, passwordHash, failedAttempts, blocked);
    }
}
