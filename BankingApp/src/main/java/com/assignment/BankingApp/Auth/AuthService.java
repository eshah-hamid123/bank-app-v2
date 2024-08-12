package com.assignment.BankingApp.Auth;
import com.assignment.BankingApp.security.JwtHelper;
import com.assignment.BankingApp.security.JwtResponse;
import com.assignment.BankingApp.user.User;
import com.assignment.BankingApp.user.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserDetailsService userDetailsService,
                       AuthenticationManager manager,
                       JwtHelper helper,
                       UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.manager = manager;
        this.helper = helper;
        this.userRepository = userRepository;
    }

    public JwtResponse login(String username, String password) {
        User user = null;
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            user = new User(optionalUser.get());
            if (!user.getIsActive()) {
                throw new BadCredentialsException("Invalid username or password");
            }
        }

        this.doAuthenticate(username, password);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = this.helper.generateToken(userDetails);

        return JwtResponse.builder()
                .jwtToken(token)
                .user(user)
                .build();
    }

    private void doAuthenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        manager.authenticate(authentication);
    }
}

