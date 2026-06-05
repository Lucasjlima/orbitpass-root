package fiap.com.br.orbitpasscore.vectorstore.controller;

import fiap.com.br.orbitpasscore.vectorstore.dto.request.ChatRequest;
import fiap.com.br.orbitpasscore.vectorstore.dto.response.ChatResponse;
import fiap.com.br.orbitpasscore.vectorstore.service.VectorStoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vector-store")
@RequiredArgsConstructor
public class VectorStoreController {

    private final VectorStoreService vectorStoreService;

    @PostMapping("/seed")
    public ResponseEntity<Void> seed() {
        vectorStoreService.seedDocuments();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String response = vectorStoreService.chat(request.message());
        return ResponseEntity.ok(new ChatResponse(response));
    }
}
