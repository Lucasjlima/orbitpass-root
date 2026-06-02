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
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest request) {
        User saved = userService.createUser(UserMapper.toEntity(request));
        return ResponseEntity
                .created(URI.create("/api/users/" + saved.getId()))
                .body(UserMapper.toResponse(saved));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.findById(id)));
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList());
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request) {
        User updated = userService.updateUser(id, UserMapper.toEntity(request));
        return ResponseEntity.ok(UserMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
