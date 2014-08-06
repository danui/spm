package com.apfrank.spm;

/**
 * Exception for the incorrect use of SPM. This enables Main to
 * differentiate between user errors on the command-line from
 * other errors.  For the former, we prefer to just print the
 * message and avoid a stack trace.
 */
public class UsageException extends RuntimeException {
    public UsageException(String message) {
        super(message);
    }
}
