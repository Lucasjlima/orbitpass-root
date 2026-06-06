package fiap.com.br.orbitpasscore.vectorstore.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.orbitpasscore.vectorstore.exception.ChatbotException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private static final String DESTINATIONS_RESOURCE = "destinations.json";
    private static final String TOUR_AVAILABILITY_TOOL = "buscarToursDisponiveis";

    private static final String SYSTEM_INSTRUCTIONS = """
            You are an OrbitPass space travel assistant with access to medical safety information about \
            space destinations and a tool to check real-time tour availability.
            
            Response Priority:
            
            1. First identify the user's primary intent.
            2. Answer the user's primary question directly and concisely.
            3. Use medical information only when it is relevant to the user's request.
            
            Medical Safety Protocol:
            
            Provide medical guidance only if:
            - the user mentions a medical condition or disability;
            - the user asks about safety, eligibility, fitness requirements, or medical restrictions;
            - the destination has an absolute contraindication directly related to information provided by the user.
            
            When medical guidance is required:
            - clearly explain the relevant restriction or warning;
            - if an absolute contraindication exists, strongly advise against the trip;
            - then continue with the rest of the requested information.
            
            When medical guidance is NOT required:
            - do not mention medical restrictions;
            - do not summarize medical requirements;
            - do not proactively provide health warnings.
            
            For availability, dates, pricing, itineraries, or destination information:
            - answer the requested information first;
            - use the availability tool when needed;
            - keep the response focused on the user's question.
            
            When presenting tour availability results obtained from tools:
            
            - Always include departure date.
            - Always include return date when available.
            - Always include remaining seats when available.
            - Always include price when available.
            - Always include duration when available.
            - Never omit information returned by the availability tool.
            
            Context (medical and destination information):
            """;
    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public void seedDocuments() {
        try {
            List<Map<String, String>> entries = readDestinations();

            List<Document> documents = entries.stream()
                    .map(entry -> {
                        String content = entry.get("content");
                        String name = entry.get("name");
                        UUID id = UUID.nameUUIDFromBytes(content.getBytes(StandardCharsets.UTF_8));
                        return new Document(id.toString(), content, Map.of("name", name));
                    })
                    .toList();

            vectorStore.add(documents);
        } catch (ChatbotException e) {
            throw e;
        } catch (Exception e) {
            throw new ChatbotException("Failed to seed documents", e);
        }
    }

    public String chat(String userMessage) {
        try {
            List<Document> context = vectorStore.similaritySearch(
                    SearchRequest.builder().query(userMessage).topK(3).build());

            String contextText = context.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n\n"));

            SystemMessage systemMessage = new SystemMessage(
                    SYSTEM_INSTRUCTIONS + "\n" + contextText);
            UserMessage humanMessage = new UserMessage(userMessage);

            ToolCallingChatOptions options = ToolCallingChatOptions.builder()
                    .toolNames(TOUR_AVAILABILITY_TOOL)
                    .build();

            Prompt prompt = new Prompt(List.of((Message) systemMessage, humanMessage), options);

            return chatModel.call(prompt).getResult().getOutput().getText();
        } catch (ChatbotException e) {
            throw e;
        } catch (Exception e) {
            throw new ChatbotException("Failed to process chat request", e);
        }
    }

    private List<Map<String, String>> readDestinations() {
        try (InputStream in = new ClassPathResource(DESTINATIONS_RESOURCE).getInputStream()) {
            return objectMapper.readValue(in, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + DESTINATIONS_RESOURCE, e);
        }
    }
}
