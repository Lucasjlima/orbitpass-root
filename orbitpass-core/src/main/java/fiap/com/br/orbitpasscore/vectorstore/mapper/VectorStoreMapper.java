package fiap.com.br.orbitpasscore.vectorstore.mapper;

import fiap.com.br.orbitpasscore.vectorstore.dto.request.VectorStoreRequest;
import fiap.com.br.orbitpasscore.vectorstore.dto.response.VectorStoreResponse;
import fiap.com.br.orbitpasscore.vectorstore.entity.VectorStore;

public final class VectorStoreMapper {

    private VectorStoreMapper() {
    }

    public static VectorStore toEntity(VectorStoreRequest request) {
        return VectorStore.builder()
                .content(request.content())
                .metadata(request.metadata())
                .embedding(request.embedding())
                .build();
    }

    public static VectorStoreResponse toResponse(VectorStore vectorStore) {
        return new VectorStoreResponse(
                vectorStore.getId(),
                vectorStore.getContent(),
                vectorStore.getMetadata()
        );
    }
}
