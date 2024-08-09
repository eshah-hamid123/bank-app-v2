package com.assignment.BankingApp.account;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = Objects.requireNonNull(accountService, "Account Service must not be null");
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@Valid @RequestBody Account account) {
        try {
            Account newAccount = accountService.createAccount(account);
            return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating account", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyAuthority('admin', 'account-holder')")
    @GetMapping("/get-account/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable Long accountId) {
        try {
            Optional<Account> userAccount = accountService.getAccountById(accountId);
            if (userAccount.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(userAccount.get());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PutMapping("/edit-account/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long accountId, @Valid @RequestBody Account updatedAccount) {
        Account updated = accountService.updateAccount(accountId, updatedAccount);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @DeleteMapping("/delete-account/{accountId}")
    public ResponseEntity<?> deactivateAccount(@PathVariable Long accountId) {
        try {
            accountService.deactivateAccount(accountId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/all-accounts")
    public ResponseEntity<List<Account>> getAllAccounts(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(name = "size", defaultValue = "1000") Integer size) {
        List<Account> accounts = accountService.findAll(page, size);
        return ResponseEntity.ok(accounts);
    }
}
