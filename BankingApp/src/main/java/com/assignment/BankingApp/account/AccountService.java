package com.assignment.BankingApp.account;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int ACCOUNT_NUMBER_LENGTH = 8;
    private static final int MAX_PAGE_SIZE = 1000;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account createAccount(Account account) {
        if (accountRepository.existsByUsername(account.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new DataIntegrityViolationException("Account number already exists");
        }

        if (account.getBalance() <= 0) {
            throw new IllegalArgumentException("Balance must be greater than 0");
        }

        if (account.getAccountNumber().length() != ACCOUNT_NUMBER_LENGTH) {
            throw new IllegalArgumentException("Account number must be exactly " + ACCOUNT_NUMBER_LENGTH + " digits long");
        }

        account.setRole("account-holder");
        //account.setPassword(account.getPassword());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setIsActive(true);

        return accountRepository.save(account);
    }

    public Optional<Account> getAccountById(Long accountId) {
        Account account = getCurrentLoggedInUser();
        if (account.getId().equals(accountId) || account.getId().equals(1L)) {
            return accountRepository.findById(accountId);
        } else {
            throw new AccessDeniedException("You are not authorized to access this account.");
        }
    }

    public Account updateAccount(Long accountId, Account updatedAccount) {
        Optional<Account> existingAccount = accountRepository.findById(accountId);

        if (existingAccount.isPresent()) {
            Account accountToUpdate = existingAccount.get();
            accountToUpdate.setUsername(updatedAccount.getUsername());
            accountToUpdate.setPassword(updatedAccount.getPassword());
            accountToUpdate.setBalance(updatedAccount.getBalance());
            accountToUpdate.setPassword(passwordEncoder.encode(updatedAccount.getPassword()));
            accountToUpdate.setAddress(updatedAccount.getAddress());
            accountToUpdate.setEmail(updatedAccount.getEmail());
            return accountRepository.save(accountToUpdate);
        }
        return null;
    }

    public void deactivateAccount(Long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setIsActive(false);
            account.setBalance(0L);
            accountRepository.save(account);
        } else {
            throw new EntityNotFoundException("Account with id " + accountId + " not found");
        }
    }

    public List<Account> findAll(Integer page, Integer size) {
        if (page < 0) {
            page = 0;
        }
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        Page<Account> accountPage = accountRepository.findAll(PageRequest.of(page, size));
        return accountPage.stream()
                .filter(Account::getIsActive)
                .collect(Collectors.toList());
    }


    private Account getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        return accountRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
