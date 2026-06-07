package fiap.com.br.orbitpasscore.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.com.br.orbitpasscore.security.TokenService;
import fiap.com.br.orbitpasscore.ticket.messaging.PaymentRequestPublisher;
import fiap.com.br.orbitpasscore.ticket.repository.TicketRepository;
import fiap.com.br.orbitpasscore.ticket.service.TicketService;
import fiap.com.br.orbitpasscore.tour.repository.TourRepository;
import fiap.com.br.orbitpasscore.tour.service.TourService;
import fiap.com.br.orbitpasscore.tourdate.repository.TourDateRepository;
import fiap.com.br.orbitpasscore.tourdate.service.TourDateService;
import fiap.com.br.orbitpasscore.user.entity.Role;
import fiap.com.br.orbitpasscore.user.entity.User;
import fiap.com.br.orbitpasscore.user.repository.UserRepository;
import fiap.com.br.orbitpasscore.user.service.AuthService;
import fiap.com.br.orbitpasscore.user.service.UserService;
import fiap.com.br.orbitpasscore.vectorstore.service.VectorStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.cache.type=none")
@AutoConfigureMockMvc
public abstract class BaseE2ETest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TokenService tokenService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected TourService tourService;

    @MockBean
    protected TourDateService tourDateService;

    @MockBean
    protected TicketService ticketService;

    @MockBean
    protected VectorStoreService vectorStoreService;

    @MockBean
    protected ChatModel chatModel;

    @MockBean
    protected VectorStore vectorStore;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected TourRepository tourRepository;

    @MockBean
    protected TourDateRepository tourDateRepository;

    @MockBean
    protected TicketRepository ticketRepository;

    @MockBean
    protected PaymentRequestPublisher paymentRequestPublisher;

    protected User adminUser;
    protected User defaultUser;

    protected String adminToken;
    protected String defaultToken;

    @BeforeEach
    void baseSetup() {
        adminUser = User.builder()
                .id(1L)
                .name("Admin User")
                .email("admin@orbitpass.com")
                .password("admin123")
                .role(Role.ADMIN)
                .build();

        defaultUser = User.builder()
                .id(2L)
                .name("Default User")
                .email("user@orbitpass.com")
                .password("user123")
                .role(Role.DEFAULT_USER)
                .build();

        // Stub userRepository to support authenticating requests via signed JWTs
        when(userRepository.findByEmail("admin@orbitpass.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findByEmail("user@orbitpass.com")).thenReturn(Optional.of(defaultUser));

        // Generate valid, signed JWT tokens
        adminToken = "Bearer " + tokenService.generateToken(adminUser);
        defaultToken = "Bearer " + tokenService.generateToken(defaultUser);
    }
}
