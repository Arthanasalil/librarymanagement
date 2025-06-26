package com.library;

public class User {
    public String username;
    public String role; // "user" or "admin"

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }
}