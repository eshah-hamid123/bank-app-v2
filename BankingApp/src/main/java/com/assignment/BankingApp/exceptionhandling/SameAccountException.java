package com.assignment.BankingApp.exceptionhandling;

public class SameAccountException extends RuntimeException {
    public SameAccountException(String message) {
        super(message);
    }
}

