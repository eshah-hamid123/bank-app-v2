package com.assignment.BankingApp.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountDTO {
    private Long id;

    private String username;

    private String password;

    private String email;

    private String address;

    private Long balance;

    private String accountNumber;
}
