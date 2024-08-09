package com.assignment.BankingApp.transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDTO {
    private Long id;

    private String description;
    private Long amount;

    private String recieverAccountNumber;
}
