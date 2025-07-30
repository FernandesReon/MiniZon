package com.example.backend.views;

public class Views {
    public static class Public {} // For non-admin users
    public static class Admin extends Public {} // For admin users, extends Public to include all public fields
}