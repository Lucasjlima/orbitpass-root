package fiap.com.br.orbitpasscore.user.controller;

import fiap.com.br.orbitpasscore.security.UserDetailsImpl;
import fiap.com.br.orbitpasscore.user.dto.request.LoginRequest;
import fiap.com.br.orbitpasscore.user.dto.request.UserRequest;
import fiap.com.br.orbitpasscore.user.dto.request.UserUpdateRequest;
import fiap.com.br.orbitpasscore.user.dto.response.LoginResponse;
import fiap.com.br.orbitpasscore.user.dto.response.UserResponse;
import fiap.com.br.orbitpasscore.user.entity.Role;
import fiap.com.br.orbitpasscore.user.entity.User;
import fiap.com.br.orbitpasscore.user.mapper.UserMapper;
import fiap.com.br.orbitpasscore.user.service.AuthService;
import fiap.com.br.orbitpasscore.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<UserResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        ensureOwnerOrAdmin(principal, id);
        return ResponseEntity.ok(UserMapper.toResponse(userService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        ensureOwnerOrAdmin(principal, id);
        User updated = userService.updateUser(id, UserMapper.toEntity(request));
        return ResponseEntity.ok(UserMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private void ensureOwnerOrAdmin(UserDetailsImpl principal, Long targetId) {
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        boolean isOwner = targetId.equals(principal.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
    }
}
