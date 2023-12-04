package com.example.omninventory;

/**
 * Interface for a class that can handle the results of username update.
 */
public interface UpdateUsernameHandler {
    abstract void onUsernameValidation(boolean isUnique, boolean isError, String newUsername);
}
