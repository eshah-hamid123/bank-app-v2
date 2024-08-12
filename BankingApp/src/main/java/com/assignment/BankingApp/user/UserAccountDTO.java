package com.assignment.BankingApp.user;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAccountDTO {
    private Long id;

    private String username;

    private String password;

    private String email;

    private String address;

    private Long balance;

    private String accountNumber;
}
