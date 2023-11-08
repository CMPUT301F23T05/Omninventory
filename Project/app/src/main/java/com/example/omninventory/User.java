package com.example.omninventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {

        private String name;
        private String username;
        private String password;
        private ArrayList<String> ownedItems;
        public User(String name, String username, String password, ArrayList<String> ownedItems) {
                this.name = name;
                this.username = username;
                this.password = password;
                this.ownedItems = ownedItems;
        }
        public String getName() {
                return name;
        }
        public String getUsername() {
                return username;
        }
        public String getPassword() { return password; }
        public ArrayList<String> getItemsRefs() { return ownedItems; }

}
