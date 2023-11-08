package com.example.omninventory;

// === For validating user's login credentials without
// running into logical issues due to Firebase's asynchronous operations
public interface ValidationResultCallback {
    public abstract void onValidationResult(boolean isValid, String message);
}
