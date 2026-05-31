package fiap.com.br.orbitpasscore.vectorstore.dto.response;

import java.util.UUID;

public record VectorStoreResponse(
        UUID id,
        String content,
        String metadata
) {
}
