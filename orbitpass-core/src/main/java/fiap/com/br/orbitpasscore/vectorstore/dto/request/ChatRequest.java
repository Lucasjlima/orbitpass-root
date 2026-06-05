package fiap.com.br.orbitpasscore.vectorstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "Message cannot be empty")
        @Size(max = 1000, message = "Message must not exceed 1000 characters")
        String message
) {}
