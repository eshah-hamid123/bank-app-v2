package com.assignment.BankingApp.Auth;
import com.assignment.BankingApp.account.Account;
import com.assignment.BankingApp.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public UserService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isEmpty()) {
            throw new UsernameNotFoundException("User or password incorrect.");
        }

        return new org.springframework.security.core.userdetails.User(account.get().getUsername(),
                account.get().getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList(account.get().getRole()));
    }

}
