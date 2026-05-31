package fiap.com.br.orbitpasscore.user.dto.response;

import fiap.com.br.orbitpasscore.user.entity.Role;
import java.time.Instant;
import java.util.List;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        Instant createdAt,
        String phone,
        List<Long> ticketIds
) {
}
