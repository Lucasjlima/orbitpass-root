package fiap.com.br.orbitpasscore.user.dto.response;

public record LoginResponse(String token, String tokenType) {

    public static LoginResponse bearer(String token) {
        return new LoginResponse(token, "Bearer");
    }
}
