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

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "accounts")
public class Account {
    private static final int MIN_ACCOUNT_NUMBER_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    private Long balance;

    @Size(min = MIN_ACCOUNT_NUMBER_LENGTH, message = "Account number must be at least " + MIN_ACCOUNT_NUMBER_LENGTH + " characters")
    private String accountNumber;

    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Account(Account account) {
        this.id = account.id;
        this.userId = account.userId;
        this.isActive = account.isActive;
        this.balance = account.balance;
        this.accountNumber = account.accountNumber;
        this.createdAt = account.createdAt;
        this.updatedAt = account.updatedAt;
    }

    public Account() { }

}
