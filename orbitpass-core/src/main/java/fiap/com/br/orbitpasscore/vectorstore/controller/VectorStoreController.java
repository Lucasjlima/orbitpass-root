package fiap.com.br.orbitpasscore.vectorstore.controller;

import fiap.com.br.orbitpasscore.vectorstore.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vector-store")
@RequiredArgsConstructor
public class VectorStoreController {

    private final VectorStoreService vectorStoreService;
}
