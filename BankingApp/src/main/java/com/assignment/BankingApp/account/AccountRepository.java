package com.assignment.BankingApp.account;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
     Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByAccountNumber(String accountNumber);
    List<Account> findByIsActive(Boolean isActive, Pageable pageable);

}
