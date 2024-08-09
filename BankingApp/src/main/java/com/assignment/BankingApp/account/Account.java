package com.assignment.BankingApp.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "accounts")
public class Account {
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_ACCOUNT_NUMBER_LENGTH = 8;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    private String username;

    @Size(min = MIN_PASSWORD_LENGTH, message = "Password must be at least " + MIN_PASSWORD_LENGTH + " characters")
    private String password;

    private String role;

    @Email(message = "Email should be valid")
    private String email;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    private String address;

    private Long balance;

    @Size(min = MIN_ACCOUNT_NUMBER_LENGTH, message = "Account number must be at least " + MIN_ACCOUNT_NUMBER_LENGTH + " characters")
    private String accountNumber;

    public Account(Account account) {
        this.id = account.id;
        this.username = account.username;
        this.password = account.password;
        this.role = account.role;
        this.email = account.email;
        this.address = account.address;
        this.isActive = account.isActive;
        this.balance = account.balance;
        this.accountNumber = account.accountNumber;
    }

    public Account() { }




}
