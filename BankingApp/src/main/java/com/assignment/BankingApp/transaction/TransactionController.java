package com.assignment.BankingApp.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PreAuthorize("hasAnyAuthority('account-holder')")
    @PostMapping
    public ResponseEntity<?> postTransaction(@RequestBody TransactionDTO transaction) {
        try {
            TransactionHistoryDTO savedTransaction = transactionService.createTransaction(transaction);
            return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping
    public ResponseEntity<List<TransactionHistoryDTO>> getAllTransactions(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(name = "size", defaultValue = "1000") Integer size) {
        List<TransactionHistoryDTO> transactions = transactionService.findAll(page, size);
        return ResponseEntity.ok(transactions);
    }
//
//    @GetMapping("/{transactionId}")
//    public ResponseEntity<Transaction> getTransactionById(@PathVariable("transactionId") Long transactionId) {
//        Optional<Transaction> transaction = transactionService.getTransactionById(transactionId);
//        if (transaction.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(transaction.get());
//    }
//
    @GetMapping("/get-debit-transactions")
    public ResponseEntity<?> getDebitTransactions() {
        try {
            List<TransactionHistoryDTO> transactions = transactionService.getDebitTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/get-credit-transactions")
    public ResponseEntity<?> getCreditTransactions() {
        try {
            List<TransactionHistoryDTO> transactions = transactionService.getCreditTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
