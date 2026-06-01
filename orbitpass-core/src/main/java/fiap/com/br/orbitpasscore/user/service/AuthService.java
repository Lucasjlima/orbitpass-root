package fiap.com.br.orbitpasscore.user.service;

import fiap.com.br.orbitpasscore.security.TokenService;
import fiap.com.br.orbitpasscore.security.UserDetailsImpl;
import fiap.com.br.orbitpasscore.user.dto.request.LoginRequest;
import fiap.com.br.orbitpasscore.user.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return LoginResponse.bearer(tokenService.generateToken(principal.getUser()));
    }
}
