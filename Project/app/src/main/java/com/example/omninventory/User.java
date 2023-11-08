package com.example.omninventory;

import java.io.Serializable;

public class User implements Serializable {
        private String username;
        // todo: hash password
        private String password;
        public User(String username, String password) {
                this.username = username;
                this.password = password;
        }
        public String getUsername() {
                return username;
        }
        public String getPassword() { return password; }
}
