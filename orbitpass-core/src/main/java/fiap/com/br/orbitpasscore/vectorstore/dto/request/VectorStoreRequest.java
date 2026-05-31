package fiap.com.br.orbitpasscore.vectorstore.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VectorStoreRequest(
        @NotBlank String content,
        String metadata,
        float[] embedding
) {
}
