package org.example;

import org.example.Repository.JdbcSetRepository;
import org.example.Repository.JdbcCardRepository;

public class Main {
    public static void main(String[] args) {
        try {
            DBSetUp.ensureSchema();

            JdbcSetRepository setRepo = new JdbcSetRepository();
            JdbcCardRepository cardRepo = new JdbcCardRepository();

            // (app logic will go here)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

