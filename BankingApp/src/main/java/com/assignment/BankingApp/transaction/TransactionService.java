package com.assignment.BankingApp.transaction;

import com.assignment.BankingApp.account.Account;
import com.assignment.BankingApp.account.AccountRepository;
import com.assignment.BankingApp.config.ApiSecurityConfiguration;
import com.assignment.BankingApp.exceptionhandling.AccountNotFoundException;
import com.assignment.BankingApp.exceptionhandling.InsufficientBalanceException;
import com.assignment.BankingApp.exceptionhandling.SameAccountException;
import com.assignment.BankingApp.user.User;
import com.assignment.BankingApp.user.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiSecurityConfiguration.class);
    private static final int MAX_PAGE_SIZE = 1000;

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionHistoryDTO createTransaction(TransactionDTO newTransaction) throws Exception {

        User senderUser;
        try {
            senderUser = getCurrentLoggedInUser();
        } catch (Exception e) {
            // Handle the exception and rethrow it with a specific message
            throw new Exception("Failed to get the current logged-in user: " + e.getMessage(), e);
        }
            Account senderAccount = null;
            Optional <Account> optionalAccount = accountRepository.findByUserId(senderUser.getId());
            if (optionalAccount.isPresent()) {
                senderAccount = optionalAccount.get();
            }
            Account receiverAccount = getAccountByNumber(newTransaction.getRecieverAccountNumber());
            User recieverUser = userRepository.findById(receiverAccount.getUserId()).get();
            if (senderAccount.equals(receiverAccount)) {
                throw new SameAccountException("You can't transfer money to your account");
            }
            if (senderAccount.getBalance() < newTransaction.getAmount()) {
                throw new InsufficientBalanceException("Insufficient balance in sender account");
            }
            if (newTransaction.getAmount() < 0) {
                throw new IllegalArgumentException("Amount should be greater than 0");
            }

            updateAccountBalances(senderAccount, receiverAccount, newTransaction.getAmount());

            Transaction savedTransaction = saveTransaction(newTransaction, senderAccount, receiverAccount);
            return new TransactionHistoryDTO(
                    savedTransaction.getId(),
                    savedTransaction.getDescription(),
                    savedTransaction.getAmount(),
                    savedTransaction.getDate(),
                    senderUser.getUsername(),
                    recieverUser.getUsername()
            );

    }

//    public List<TransactionHistoryDTO> findAll(Integer page, Integer size) {
//        if (page < 0) {
//            page = 0;
//        }
//        if (size > MAX_PAGE_SIZE) {
//            size = MAX_PAGE_SIZE;
//        }
//        List<Transaction> transactions = transactionRepository.findAll(PageRequest.of(page, size)).getContent();
//        return transactions.stream().map(transaction -> {
//            String senderUsername = accountRepository.findById(transaction.getSenderAccountId())
//                    .map(Account::getUsername)
//                    .orElse("Unknown");
//            String receiverUsername = accountRepository.findById(transaction.getReceiverAccountId())
//                    .map(Account::getUsername)
//                    .orElse("Unknown");
//            return new TransactionHistoryDTO(
//                    transaction.getId(),
//                    transaction.getDescription(),
//                    transaction.getAmount(),
//                    transaction.getDate(),
//                    senderUsername,
//                    receiverUsername
//            );
//        }).collect(Collectors.toList());
//    }
//
//    public Optional<Transaction> getTransactionById(Long transactionId) {
//        return transactionRepository.findById(transactionId);
//    }
//
//    public List<TransactionHistoryDTO> getDebitTransactions() {
//        Account account = getCurrentLoggedInUser();
//        List<Transaction> transactions = transactionRepository.findBySenderAccountId(account.getId());
//        return transactions.stream().map(transaction -> {
//            String receiverUsername = accountRepository.findById(transaction.getReceiverAccountId())
//                    .map(Account::getUsername)
//                    .orElse("Unknown");
//            return new TransactionHistoryDTO(
//                    transaction.getId(),
//                    transaction.getDescription(),
//                    transaction.getAmount(),
//                    transaction.getDate(),
//                    null,
//                    receiverUsername
//            );
//        }).collect(Collectors.toList());
//    }
//
//    public List<TransactionHistoryDTO> getCreditTransactions() {
//        Account account = getCurrentLoggedInUser();
//        List<Transaction> transactions = transactionRepository.findByReceiverAccountId(account.getId());
//        return transactions.stream().map(transaction -> {
//            String senderUsername = accountRepository.findById(transaction.getSenderAccountId())
//                    .map(Account::getUsername)
//                    .orElse("Unknown");
//            return new TransactionHistoryDTO(
//                    transaction.getId(),
//                    transaction.getDescription(),
//                    transaction.getAmount(),
//                    transaction.getDate(),
//                    senderUsername,
//                    null
//            );
//        }).collect(Collectors.toList());
//    }

    private User getCurrentLoggedInUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        return userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new Exception("User not found"));
    }

    private Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for account number: " + accountNumber));
    }

    private void updateAccountBalances(Account senderAccount, Account receiverAccount, Long amount) {
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
    }

    private Transaction saveTransaction(TransactionDTO newTransaction, Account senderAccount, Account receiverAccount) {
        Transaction transaction = new Transaction();
        transaction.setDate(new Date());
        transaction.setDescription(newTransaction.getDescription());
        transaction.setAmount(newTransaction.getAmount());
        transaction.setSenderAccountId(senderAccount.getId());
        transaction.setReceiverAccountId(receiverAccount.getId());
        return transactionRepository.save(transaction);
    }
}
