package fiap.com.br.orbitpasscore.vectorstore.service;

import fiap.com.br.orbitpasscore.vectorstore.repository.VectorStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final VectorStoreRepository vectorStoreRepository;
}
