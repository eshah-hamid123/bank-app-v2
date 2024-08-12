package com.assignment.BankingApp.security;

import com.assignment.BankingApp.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@ToString
public class JwtResponse {
    private String jwtToken;
    private User user;

    // Constructor with defensive copying
    public JwtResponse(String jwtToken, User user) {
        this.jwtToken = jwtToken;
        this.user = user == null ? null : new User(user);
    }

    public User getUser() {
        return user == null ? null : new User(user);
    }

    public static class JwtResponseBuilder {
        private String jwtToken;
        private User user;

        public JwtResponseBuilder jwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
            return this;
        }

        public JwtResponseBuilder user(User user) {
            this.user = user == null ? null : new User(user);
            return this;
        }

        public JwtResponse build() {
            return new JwtResponse(jwtToken, user == null ? null : new User(user));
        }
    }
}
