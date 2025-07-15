package com.example.agripricechecker;

public class UserModel {
    public String name;
    public String email;

    public UserModel() {}  // Required for Firebase

    public UserModel(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
