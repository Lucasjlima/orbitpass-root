package fiap.com.br.orbitpasscore.vectorstore.controller;

import fiap.com.br.orbitpasscore.vectorstore.dto.request.ChatRequest;
import fiap.com.br.orbitpasscore.vectorstore.dto.response.ChatResponse;
import fiap.com.br.orbitpasscore.vectorstore.service.VectorStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Vector Store / Chatbot", description = "Endpoints for seeding knowledge store and chatting with AI assistant")
public class VectorStoreController {

    private final VectorStoreService vectorStoreService;

    @PostMapping("/seed")
    @Operation(summary = "Seed vector store", description = "Load knowledge base documents into the vector store database (Admin only)")
    @ApiResponse(responseCode = "200", description = "Vector store seeded successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden access")
    public ResponseEntity<Void> seed() {
        vectorStoreService.seedDocuments();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chat")
    @Operation(summary = "Chat with assistant", description = "Submit a prompt message to the AI chatbot using context retrieved from vector store")
    @ApiResponse(responseCode = "200", description = "Chatbot response generated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String response = vectorStoreService.chat(request.message());
        return ResponseEntity.ok(new ChatResponse(response));
    }
}
