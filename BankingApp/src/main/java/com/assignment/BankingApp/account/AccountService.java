package com.assignment.BankingApp.account;

import com.assignment.BankingApp.user.UserAccountDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private static final int ACCOUNT_NUMBER_LENGTH = 10;
    private static final int MAX_PAGE_SIZE = 1000;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;

    }

    public Account createAccount(UserAccountDTO payload, Long userId) {

        if (accountRepository.existsByAccountNumber(payload.getAccountNumber())) {
            throw new DataIntegrityViolationException("Account number already exists");
        }

        if (payload.getBalance() <= 0) {
            throw new IllegalArgumentException("Balance must be greater than 0");
        }

        if (payload.getAccountNumber().length() != ACCOUNT_NUMBER_LENGTH) {
            throw new IllegalArgumentException("Account number must be exactly " + ACCOUNT_NUMBER_LENGTH + " digits long");
        }
        Account newAccount = new Account();
        newAccount.setAccountNumber(payload.getAccountNumber());
        newAccount.setBalance(payload.getBalance());
        newAccount.setUserId(userId);
        newAccount.setIsActive(true);

        return accountRepository.save(newAccount);
    }

    public Optional<Account> getAccountByUserId(Long userId) {
        return accountRepository.findByUserId(userId);

    }

    public void updateAccount(Long userId, Long balance) {
        Optional<Account> existingAccount = accountRepository.findByUserId(userId);
        if (existingAccount.isPresent()){
            Account accountToUpdate = existingAccount.get();
            accountToUpdate.setBalance(balance);
            accountRepository.save(accountToUpdate);
        }


    }

    public void deActivateAccount(Long userId) {
        Optional<Account> existingAccount = accountRepository.findByUserId(userId);
        if (existingAccount.isPresent()){
            Account accountToDelete = existingAccount.get();
            accountToDelete.setBalance(0L);
            accountToDelete.setIsActive(false);
            accountRepository.save(accountToDelete);
        }
    }

//
//    public List<Account> findAll(Integer page, Integer size) {
//        if (page < 0) {
//            page = 0;
//        }
//        if (size > MAX_PAGE_SIZE) {
//            size = MAX_PAGE_SIZE;
//        }
//        Page<Account> accountPage = accountRepository.findAll(PageRequest.of(page, size));
//        return accountPage.stream()
//                .filter(Account::getIsActive)
//                .collect(Collectors.toList());
//    }
//
//
//    private Account getCurrentLoggedInUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String loggedInUsername = authentication.getName();
//
//        return accountRepository.findByUsername(loggedInUsername)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }
}
