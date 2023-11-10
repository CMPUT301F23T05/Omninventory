package com.example.omninventory;

/**
 * For validating user's login credentials without running into logical issues due to Firebase's
 * asynchronous operations. Similar usage as GetInventoryItemHandler and InventoryUpdateHandler.
 * @author Rose
 */
public interface ValidationResultCallback {
    public abstract void onValidationResult(boolean isValid, String message);
}
