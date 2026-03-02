package com.arp.dimdimdigaana.user.controller;

import com.arp.dimdimdigaana.user.dto.UserRequestDto;
import com.arp.dimdimdigaana.user.dto.UserResponseDto;
import com.arp.dimdimdigaana.user.dto.UserSearchRequest;
import com.arp.dimdimdigaana.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * POST /api/users
     * Create a new user.
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto request) {
        log.info("POST /api/users - createUser request received for username: {}", request.getUsername());
        UserResponseDto response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/users/{id}
     * Retrieve a user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{} - getUserById request received", id);
        UserResponseDto response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/users
     * Retrieve all users.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        log.info("GET /api/users - getAllUsers request received");
        List<UserResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/users/search
     * Search users by dynamic filter criteria.
     */
    @PostMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@Valid @RequestBody UserSearchRequest request) {
        log.info("POST /api/users/search - searchUsers request received with {} criteria",
                request.getCriteria() == null ? 0 : request.getCriteria().size());
        List<UserResponseDto> response = userService.searchUsers(request.getCriteria());
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/users/{id}
     * Update an existing user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto request) {
        log.info("PUT /api/users/{} - updateUser request received", id);
        UserResponseDto response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/users/{id}
     * Delete a user by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - deleteUser request received", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

