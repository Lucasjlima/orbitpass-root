package fiap.com.br.orbitpasscore.user.dto.request;

import fiap.com.br.orbitpasscore.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull Role role,
        String phone
) {
}
