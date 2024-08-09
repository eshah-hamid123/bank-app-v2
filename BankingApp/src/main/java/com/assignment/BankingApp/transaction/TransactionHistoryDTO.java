package com.assignment.BankingApp.transaction;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TransactionHistoryDTO {
    private Long id;
    private String description;
    private Long amount;
    private Date date;
    private String senderUsername;
    private String receiverUsername;

    public TransactionHistoryDTO(Long id, String description, Long amount, Date date, String senderUsername, String receiverUsername) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date == null ? null : new Date(date.getTime());
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
    }

    public Date getDate() {
        return date == null ? null : new Date(date.getTime());
    }

    public void setDate(Date date) {
        this.date = date == null ? null : new Date(date.getTime());
    }
}
