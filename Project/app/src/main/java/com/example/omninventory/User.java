package com.example.omninventory;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
        private String username;
        private String password;
        private ArrayList<String> ownedItems;
        public User(String username, String password, ArrayList<String> ownedItems) {
                this.username = username;
                this.password = password;
                this.ownedItems = ownedItems;
        }
        public String getUsername() {
                return username;
        }
        public String getPassword() { return password; }
        public ArrayList<String> getItemsRefs() { return ownedItems; }

}
