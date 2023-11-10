package com.example.omninventory;

/**
 * Interface used by LoginActivity and SignupActivity to validate and authenticate login/signup credentials
 *
 * @author Rose Nguyen
 */
public interface ValidationResultCallback {
    public abstract void onValidationResult(boolean isValid, String message, User user);
}
