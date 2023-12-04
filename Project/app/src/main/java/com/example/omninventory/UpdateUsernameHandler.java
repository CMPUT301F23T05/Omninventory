package com.example.omninventory;

public interface UpdateUsernameHandler {
    abstract void onUsernameValidation(boolean isUnique, boolean isError, String newUsername);
}
