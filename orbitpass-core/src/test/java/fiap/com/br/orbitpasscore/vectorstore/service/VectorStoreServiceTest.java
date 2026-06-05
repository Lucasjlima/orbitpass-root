package fiap.com.br.orbitpasscore.vectorstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.orbitpasscore.vectorstore.exception.ChatbotException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class VectorStoreServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatModel chatModel;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private VectorStoreService vectorStoreService;

    @Test
    void chat_returnsResponse_whenValidMessage() {
        Document doc1 = new Document("Mars has restrictions for cardiac patients.");
        Document doc2 = new Document("Mars tours depart from the Cape Canaveral orbital hub.");
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc1, doc2));

        ChatResponse mockChatResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        AssistantMessage mockOutput = mock(AssistantMessage.class);
        when(mockOutput.getText()).thenReturn("Marte tem restrições para cardíacos.");
        when(mockGeneration.getOutput()).thenReturn(mockOutput);
        when(mockChatResponse.getResult()).thenReturn(mockGeneration);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        String result = vectorStoreService.chat("I have a heart condition, can I travel to Mars?");

        assertThat(result).isEqualTo("Marte tem restrições para cardíacos.");
    }

    @Test
    void chat_throwsChatbotException_whenModelFails() {
        Document doc1 = new Document("Some context about Mars");
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc1));
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("model down"));

        assertThatThrownBy(() -> vectorStoreService.chat("What are Mars restrictions?"))
                .isInstanceOf(ChatbotException.class)
                .hasMessageContaining("Failed to process chat request");
    }

    @Test
    void seedDocuments_throwsChatbotException_whenResourceMissing() throws IOException {
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenThrow(new IOException("boom"));

        assertThatThrownBy(() -> vectorStoreService.seedDocuments())
                .isInstanceOf(ChatbotException.class)
                .hasMessageContaining("Failed to seed documents");
    }
}
