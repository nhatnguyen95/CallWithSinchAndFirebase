package com.example.nhatnguyen.caller;

/**
 * Created by Nhat Nguyen on 08-Apr-16.
 */
public class User {
    private String email;
    private String phoneNumber;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    public User(){

    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
