package org.example;

public class Main {
    public static void main(String[] args) {
        try {
            DBSetUp.ensureSchema();
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
}
