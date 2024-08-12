package com.assignment.BankingApp.user;
import com.assignment.BankingApp.account.Account;
import com.assignment.BankingApp.account.AccountService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
        private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

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

        public User updateUser(Long userId, UserAccountDTO payload) {
        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            userToUpdate.setUsername(payload.getUsername());
            userToUpdate.setAddress(payload.getAddress());
            userToUpdate.setEmail(payload.getEmail());

            accountService.updateAccount(userToUpdate.getId(), payload.getBalance());
            return userRepository.save(userToUpdate);
        }
        return null;
    }

    public void deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(false);
            userRepository.save(user);

            accountService.deActivateAccount(userId);


        } else {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
    }

    public UserAccountDTO getUserById(Long userId) throws Exception {
        User currentLoggedInUser = getCurrentLoggedInUser();
        if (currentLoggedInUser.getId().equals(userId) || currentLoggedInUser.getId().equals(1L)) {
            Optional<User> userToGet =  userRepository.findById(userId);
            if (userToGet.isPresent()) {
                User user = userToGet.get();
                Optional<Account> accountAgainstUser = accountService.getAccountByUserId(user.getId());
                if (accountAgainstUser.isPresent()) {
                    Account account = accountAgainstUser.get();
                    return new UserAccountDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getPassword(),
                            user.getEmail(),
                            user.getAddress(),
                            account.getBalance(),
                            account.getAccountNumber()
                    );
                }
            }


        } else {
            throw new AccessDeniedException("You are not authorized to access this account.");
        }
        return null;
    }

    private User getCurrentLoggedInUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        return userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new Exception("User not found"));
    }



}
