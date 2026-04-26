package com.samtech.carapp;

public class User {
    private final int id;
    private final String username;
    private final String role;
    private final String email;
    private final String phone;

    public User(int id, String username, String role) {
        this(id, username, role, "", "");
    }

    public User(int id, String username, String role, String email, String phone) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
