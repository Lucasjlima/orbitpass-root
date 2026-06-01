package fiap.com.br.orbitpasscore.user.mapper;

import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import fiap.com.br.orbitpasscore.user.dto.request.UserRequest;
import fiap.com.br.orbitpasscore.user.dto.request.UserUpdateRequest;
import fiap.com.br.orbitpasscore.user.dto.response.UserResponse;
import fiap.com.br.orbitpasscore.user.entity.Role;
import fiap.com.br.orbitpasscore.user.entity.User;
import java.util.List;
import java.util.Optional;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(UserRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .role(Role.DEFAULT_USER)
                .phone(request.phone())
                .build();
    }

    public static User toEntity(UserUpdateRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .build();
    }

    public static UserResponse toResponse(User user) {
        List<Long> ticketIds = Optional.ofNullable(user.getTickets())
                .orElseGet(List::of)
                .stream()
                .map(Ticket::getId)
                .toList();

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getPhone(),
                ticketIds
        );
    }
}
