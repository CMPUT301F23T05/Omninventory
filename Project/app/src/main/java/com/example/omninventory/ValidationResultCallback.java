package com.example.omninventory;

// === To work around Firebase's asynchronous operations when validating user's login/signup input
public interface ValidationResultCallback {
    public abstract void onValidationResult(boolean isValid, String message, User user);
}
