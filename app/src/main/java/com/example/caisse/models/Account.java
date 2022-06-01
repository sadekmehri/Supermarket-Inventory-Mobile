package com.example.caisse.models;


import androidx.annotation.NonNull;

public class Account {

    private Staff staff;
    private String email;
    private String role;

    public Account() {
    }

    public Account(Staff staff, String email, String role) {
        this.staff = staff;
        this.email = email;
        this.role = role;
    }

    @NonNull
    @Override
    public String toString() {
        return "Account{" +
                "staff=" + staff +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
