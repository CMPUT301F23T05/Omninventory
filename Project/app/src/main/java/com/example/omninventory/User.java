package com.example.omninventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class representing a user of the app. Holds all data stored in fields for each user.
 *
 * @author Rose
 */
public class User implements Serializable {

        private String name;
        private String username;
        private String password;
        private ArrayList<String> ownedItems;

        /**
         * Constructor that initializes a new User.
         * @param name       Name of user.
         * @param username   Username of user (the unique ID in Firestore).
         * @param password   Password of user.
         * @param ownedItems An ArrayList containing the ID of each item the User owns.
         */
        public User(String name, String username, String password, ArrayList<String> ownedItems) {
                this.name = name;
                this.username = username;
                this.password = password;
                this.ownedItems = ownedItems;
        }

        /**
         * Getter for the User's name.
         * @return The User's name.
         */
        public String getName() {
                return name;
        }

        /**
         * Getter for the User's username.
         * @return The User's username.
         */
        public String getUsername() {
                return username;
        }

        /**
         * Getter for the User's password (which should be stored as a hash).
         * @return The User's password.
         */
        public String getPassword() { return password; }

        /**
         * Getter for the User's list of owned items.
         * @return The User's list of owned items, as an ArrayList of InventoryItem IDs.
         */
        public ArrayList<String> getItemsRefs() { return ownedItems; }

        public void setName(String name) {
                this.name = name;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public HashMap<String, Object> convertToHashMap() {
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("name", this.getName());
                userData.put("username", this.getUsername());
                userData.put("password", this.getPassword());
                userData.put("ownedItems", this.getItemsRefs());
                return userData;
        }

}
