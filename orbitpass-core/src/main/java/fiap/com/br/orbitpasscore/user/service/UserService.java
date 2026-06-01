package fiap.com.br.orbitpasscore.user.service;

import fiap.com.br.orbitpasscore.user.entity.User;
import fiap.com.br.orbitpasscore.user.exception.EmailAlreadyRegisteredException;
import fiap.com.br.orbitpasscore.user.exception.UserNotFoundException;
import fiap.com.br.orbitpasscore.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        userRepository.findByEmail(user.getEmail()).ifPresent(existing -> {
            throw new EmailAlreadyRegisteredException(existing.getEmail());
        });
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(Instant.now());
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, User updated) {
        User existing = findById(id);
        if (!existing.getEmail().equals(updated.getEmail())) {
            userRepository.findByEmail(updated.getEmail()).ifPresent(other -> {
                throw new EmailAlreadyRegisteredException(other.getEmail());
            });
        }
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        return userRepository.save(existing);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
