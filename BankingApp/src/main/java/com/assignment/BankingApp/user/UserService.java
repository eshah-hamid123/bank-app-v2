package com.assignment.BankingApp.user;
import com.assignment.BankingApp.account.Account;
import com.assignment.BankingApp.account.AccountService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
        private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    private static final int ACCOUNT_NUMBER_LENGTH = 8;
    private static final int MAX_PAGE_SIZE = 1000;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AccountService accountService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    public void createUser(UserAccountDTO payload) {
        if (userRepository.existsByUsername(payload.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        User newUser = new User();
        newUser.setUsername(payload.getUsername());
        newUser.setEmail(payload.getEmail());
        newUser.setAddress(payload.getAddress());
        newUser.setRole("account-holder");
        newUser.setPassword(passwordEncoder.encode(payload.getPassword()));
        newUser.setIsActive(true);

        User createdUser =  userRepository.save(newUser);
        Account createdAccount = accountService.createAccount(payload, createdUser.getId());


    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User or password incorrect.");
        }

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(),
                user.get().getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList(user.get().getRole()));
    }
}
