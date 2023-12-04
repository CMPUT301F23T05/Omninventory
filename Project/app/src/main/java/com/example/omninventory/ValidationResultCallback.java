package com.example.omninventory;

/**
 * For validating user's login credentials without running into logical issues due to Firebase's
 * asynchronous operations. Used by LoginActivity, which needs it to return the result of validation
 * after querying the database (see LoginActivity.validateUserInput()). Similar usage as
 * GetInventoryItemHandler and InventoryUpdateHandler.
 * @author Rose
 */
public interface ValidationResultCallback {
    /**
     * Interface function to be called when validation is completed by
     * LoginActivity.validateUserInput().
     * @param isValid The result of the validation.
     * @param message An error message or some other description to be handled by the implementing
     *                class.
     */
    void onValidationResult(boolean isValid, String message, User user);
}
