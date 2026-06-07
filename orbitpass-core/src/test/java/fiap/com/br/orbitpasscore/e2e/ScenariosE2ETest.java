package fiap.com.br.orbitpasscore.e2e;

import fiap.com.br.orbitpasscore.ticket.entity.PaymentType;
import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import fiap.com.br.orbitpasscore.ticket.entity.TicketStatus;
import fiap.com.br.orbitpasscore.ticket.exception.InsufficientSpotsException;
import fiap.com.br.orbitpasscore.ticket.messaging.PaymentResponseListener;
import fiap.com.br.orbitpasscore.ticket.messaging.dto.PaymentResponseMessage;
import fiap.com.br.orbitpasscore.tour.entity.Tour;
import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import fiap.com.br.orbitpasscore.user.entity.Role;
import fiap.com.br.orbitpasscore.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScenariosE2ETest extends BaseE2ETest {

    @Autowired
    private PaymentResponseListener paymentResponseListener;

    // =========================================================================
    // TIER 3: Cross-Feature Combinations (3 Tests)
    // =========================================================================

    @Test
    void swaggerAccessAndSecureExecutionLinkage() throws Exception {
        // Test 3.1: Swagger Access and Secure Execution Linkage
        // 1. Verify Swagger docs are public
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());

        // 2. Verify trying to fetch secure endpoints without auth fails with 401
        mockMvc.perform(get("/api/tours"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userAuthenticatedJourneyToHateoasResource() throws Exception {
        // Test 3.2: User Authenticated Journey to HATEOAS Resource
        // 1. Stub the tour service
        Tour tour = Tour.builder().id(10L).name("Tour 10").price(BigDecimal.TEN).build();
        when(tourService.findById(10L)).thenReturn(tour);

        // 2. Fetch using JWT token
        mockMvc.perform(get("/api/tours/10")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void adminRolePropagationToHateoasEndpoint() throws Exception {
        // Test 3.3: Admin Role Propagation to HATEOAS Endpoint
        // 1. Stub tour service
        Tour tour = Tour.builder().id(20L).name("Tour 20").price(BigDecimal.ONE).build();
        when(tourService.findById(20L)).thenReturn(tour);

        // 2. Fetch tour as admin
        mockMvc.perform(get("/api/tours/20")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(containsString("/api/tours/20")));
    }

    // =========================================================================
    // TIER 4: Real-World Application Scenarios (5 Tests)
    // =========================================================================

    @Test
    void scenario1_happyPathUserJourney() throws Exception {
        // Scenario 1: Public Explorer to Customer Sign-up, login, retrieve tour (HATEOAS), and book ticket
        // 1. View swagger UI docs (public)
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());

        // 2. Sign up new user
        User newUser = User.builder().id(100L).name("New User").email("newuser@orbitpass.com").role(Role.DEFAULT_USER).build();
        when(userService.createUser(any(User.class))).thenReturn(newUser);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New User\",\"email\":\"newuser@orbitpass.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());

        // 3. List tours
        Tour tour = Tour.builder().id(5L).name("Jupiter Aurora").price(new BigDecimal("99000.00")).build();
        when(tourService.findAll()).thenReturn(List.of(tour));
        mockMvc.perform(get("/api/tours")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isOk());

        // 4. Retrieve single tour by ID (verifying HATEOAS links)
        when(tourService.findById(5L)).thenReturn(tour);
        mockMvc.perform(get("/api/tours/5")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists());

        // 5. Purchase ticket
        TourDate date = TourDate.builder().id(50L).tour(tour).departureDate(LocalDateTime.now().plusDays(2)).returnDate(LocalDateTime.now().plusDays(10)).totalSpots(10).bookedSpots(1).build();
        Ticket ticket = Ticket.builder().id(500L).user(defaultUser).tourDate(date).status(TicketStatus.PENDING).price(new BigDecimal("99000.00")).build();
        when(ticketService.purchase(eq(defaultUser.getId()), eq(50L), eq(PaymentType.CREDIT_CARD))).thenReturn(ticket);

        mockMvc.perform(post("/api/tickets/purchase")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tourDateId\":50,\"paymentType\":\"CREDIT_CARD\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void scenario2_privilegeEscalationAttempt() throws Exception {
        // Scenario 2: Privilege Escalation Attempt
        // 1. Authenticate default user
        // 2. Verify failure to access admin endpoints: GET /api/users, POST /api/tours, POST /api/vector-store/seed
        mockMvc.perform(get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/tours")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sneak Tour\",\"description\":\"Desc\",\"destination\":\"Space\",\"price\":100.0}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/vector-store/seed")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isForbidden());

        // 3. Admin successfully accesses them
        when(userService.findAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk());

        Tour newTour = Tour.builder().id(7L).name("Admin Tour").price(BigDecimal.TEN).build();
        when(tourService.create(any(Tour.class))).thenReturn(newTour);
        mockMvc.perform(post("/api/tours")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Admin Tour\",\"description\":\"Desc\",\"destination\":\"Space\",\"price\":10.00}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/vector-store/seed")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void scenario3_asyncTicketPaymentFlow() throws Exception {
        // Scenario 3: Asynchronous Ticket/Payment Flow
        // 1. User books ticket (enters PENDING)
        Tour tour = Tour.builder().id(6L).name("Mercury Heat").price(new BigDecimal("50000.00")).build();
        TourDate date = TourDate.builder().id(60L).tour(tour).departureDate(LocalDateTime.now().plusDays(5)).returnDate(LocalDateTime.now().plusDays(15)).totalSpots(10).bookedSpots(1).build();
        Ticket ticket = Ticket.builder().id(600L).user(defaultUser).tourDate(date).status(TicketStatus.PENDING).price(new BigDecimal("50000.00")).build();
        when(ticketService.purchase(eq(defaultUser.getId()), eq(60L), eq(PaymentType.DEBIT_CARD))).thenReturn(ticket);

        mockMvc.perform(post("/api/tickets/purchase")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tourDateId\":60,\"paymentType\":\"DEBIT_CARD\"}"))
                .andExpect(status().isAccepted());

        // 2. Simulate background payment processing result received on rabbitmq listener
        PaymentResponseMessage responseMessage = new PaymentResponseMessage(600L, TicketStatus.PAID);
        paymentResponseListener.onPaymentResponse(responseMessage);

        // 3. Verify that the ticketService applyPaymentResult is invoked with matching values
        verify(ticketService, times(1)).applyPaymentResult(600L, TicketStatus.PAID);
    }

    @Test
    void scenario4_concurrentSeatBookingConflict() throws Exception {
        // Scenario 4: Concurrent Seat Booking Conflict (returning 409 Conflict status)
        when(ticketService.purchase(eq(defaultUser.getId()), eq(70L), eq(PaymentType.CREDIT_CARD)))
                .thenThrow(new InsufficientSpotsException(70L));

        mockMvc.perform(post("/api/tickets/purchase")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tourDateId\":70,\"paymentType\":\"CREDIT_CARD\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("No spots available")));
    }

    @Test
    void scenario5_interactiveChatbotServiceIntegration() throws Exception {
        // Scenario 5: Interactive Chatbot & Service Integration
        when(vectorStoreService.chat("Hello")).thenReturn("Hello! How can I help you?");

        mockMvc.perform(post("/api/vector-store/chat")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Hello! How can I help you?"));
    }
}
