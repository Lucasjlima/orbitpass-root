package fiap.com.br.orbitpasscore.user.service;

import fiap.com.br.orbitpasscore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
}
