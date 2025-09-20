package com.ibracodeko.layerthirtyfour.models;

public class User {
    private String id;
    private String username;
    private String email;
    private UserType userType;
    private String createdAt;

    public enum UserType {
        USER("User"),
        PREMIUM("Premium"),
        ADMIN("Admin/Developer");

        private final String displayName;

        UserType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public User() {}

    public User(String id, String username, String email, UserType userType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userType = userType;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}