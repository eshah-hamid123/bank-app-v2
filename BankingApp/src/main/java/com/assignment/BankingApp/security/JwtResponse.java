package com.assignment.BankingApp.security;

import com.assignment.BankingApp.account.Account;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@ToString
public class JwtResponse {
    private String jwtToken;
    private Account account;

    // Constructor with defensive copying
    public JwtResponse(String jwtToken, Account account) {
        this.jwtToken = jwtToken;
        this.account = account == null ? null : new Account(account);
    }

    public Account getAccount() {
        return account == null ? null : new Account(account);
    }

    public static class JwtResponseBuilder {
        private String jwtToken;
        private Account account;

        public JwtResponseBuilder jwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
            return this;
        }

        public JwtResponseBuilder account(Account account) {
            this.account = account == null ? null : new Account(account);
            return this;
        }

        public JwtResponse build() {
            return new JwtResponse(jwtToken, account);
        }
    }
}
