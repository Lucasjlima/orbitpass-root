package fiap.com.br.orbitpasscore.user.controller;

import fiap.com.br.orbitpasscore.user.dto.request.LoginRequest;
import fiap.com.br.orbitpasscore.user.dto.request.UserRequest;
import fiap.com.br.orbitpasscore.user.dto.request.UserUpdateRequest;
import fiap.com.br.orbitpasscore.user.dto.response.LoginResponse;
import fiap.com.br.orbitpasscore.user.dto.response.UserResponse;
import fiap.com.br.orbitpasscore.user.entity.User;
import fiap.com.br.orbitpasscore.user.mapper.UserMapper;
import fiap.com.br.orbitpasscore.user.service.AuthService;
import fiap.com.br.orbitpasscore.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for managing user registry and authentication")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Register user", description = "Endpoint to register a new user in the system")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest request) {
        User saved = userService.createUser(UserMapper.toEntity(request));
        return ResponseEntity
                .created(URI.create("/api/users/" + saved.getId()))
                .body(UserMapper.toResponse(saved));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user credentials and retrieve a JWT token")
    @ApiResponse(responseCode = "200", description = "User authenticated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid credentials")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    @Operation(summary = "Get user by ID", description = "Retrieve details of a user by their unique identifier (Admin or self)")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden access")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.findById(id)));
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users (Admin only)")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden access")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList());
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    @Operation(summary = "Update user", description = "Update details of an existing user by their ID (Admin or self)")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "403", description = "Forbidden access")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request) {
        User updated = userService.updateUser(id, UserMapper.toEntity(request));
        return ResponseEntity.ok(UserMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Remove a user by their ID (Admin only)")
    @ApiResponse(responseCode = "244", description = "User deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden access")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
