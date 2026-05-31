package fiap.com.br.orbitpasscore.vectorstore.repository;

import fiap.com.br.orbitpasscore.vectorstore.entity.VectorStore;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VectorStoreRepository extends JpaRepository<VectorStore, UUID> {
}
