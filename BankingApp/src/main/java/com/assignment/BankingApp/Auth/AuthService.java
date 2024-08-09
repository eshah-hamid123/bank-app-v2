package com.assignment.BankingApp.Auth;

import com.assignment.BankingApp.account.Account;
import com.assignment.BankingApp.account.AccountRepository;
import com.assignment.BankingApp.security.JwtHelper;
import com.assignment.BankingApp.security.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager manager;
    private final JwtHelper helper;
    private final AccountRepository accountRepository;

    @Autowired
    public AuthService(UserDetailsService userDetailsService,
                       AuthenticationManager manager,
                       JwtHelper helper,
                       AccountRepository accountRepository) {
        this.userDetailsService = userDetailsService;
        this.manager = manager;
        this.helper = helper;
        this.accountRepository = accountRepository;
    }

    public JwtResponse login(String username, String password) {
        Account account = null;
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);
        if (optionalAccount.isPresent()) {
            account = new Account(optionalAccount.get());
            if (!account.getIsActive()) {
                throw new BadCredentialsException("Invalid username or password");
            }
        }

        this.doAuthenticate(username, password);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = this.helper.generateToken(userDetails);

        return JwtResponse.builder()
                .jwtToken(token)
                .account(account)
                .build();
    }

    private void doAuthenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        manager.authenticate(authentication);
    }
}

