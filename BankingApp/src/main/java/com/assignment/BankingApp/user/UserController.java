package com.assignment.BankingApp.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.Objects;

@RestController
@RequestMapping("/api/v2/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "User Service must not be null");
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserAccountDTO payload) {
        try {
            userService.createUser(payload);
            return new ResponseEntity<>(payload, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating account" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @Valid @RequestBody UserAccountDTO updatedUser) {
        User updated = userService.updateUser(userId, updatedUser);
        if (updated != null) {
            return ResponseEntity.ok("Information updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('admin', 'account-holder')")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserAccountDTO userAccount = userService.getUserById(userId);
            return ResponseEntity.ok(userAccount);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping
    public ResponseEntity<?> getAllAccounts(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                               @RequestParam(name = "size", defaultValue = "1000") Integer size) {
        try {
            Page<UserAccountDTO> userAccounts = userService.getAllUsers(page, size);
            return ResponseEntity.ok(userAccounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}
