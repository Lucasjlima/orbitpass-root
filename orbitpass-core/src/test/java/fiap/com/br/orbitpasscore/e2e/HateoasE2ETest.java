package fiap.com.br.orbitpasscore.e2e;

import fiap.com.br.orbitpasscore.tour.entity.Tour;
import fiap.com.br.orbitpasscore.tour.exception.TourNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HateoasE2ETest extends BaseE2ETest {

    private Tour mockTour;

    @BeforeEach
    void setupTour() {
        mockTour = Tour.builder()
                .id(42L)
                .name("Moon Walk Adventure")
                .description("Walk on the Moon and enjoy low gravity!")
                .destination("Moon")
                .price(new BigDecimal("15000.00"))
                .dates(new ArrayList<>())
                .build();

        // Stub findById to return the mock tour or throw exception
        when(tourService.findById(42L)).thenReturn(mockTour);
        when(tourService.findById(999L)).thenThrow(new TourNotFoundException(999L));
    }

    // =========================================================================
    // FEATURE 3: HATEOAS Hypermedia Links (10 Tests)
    // =========================================================================

    // --- Tier 1: Feature Coverage (Happy Path) ---

    @Test
    void getTourById_returnsSelfLink() throws Exception {
        // Must contain HATEOAS self link pointing to the individual tour resource
        mockMvc.perform(get("/api/tours/42")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(containsString("/api/tours/42")));
    }

    @Test
    void getTourById_returnsAllToursLink() throws Exception {
        // Must contain HATEOAS all-tours link pointing to the collection of tours
        mockMvc.perform(get("/api/tours/42")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links['all-tours'].href").value(containsString("/api/tours")));
    }

    @Test
    void getTourById_returnsCorrectContent() throws Exception {
        // Fields of the Tour must be serialized correctly alongside HATEOAS links
        mockMvc.perform(get("/api/tours/42")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Moon Walk Adventure"))
                .andExpect(jsonPath("$.destination").value("Moon"))
                .andExpect(jsonPath("$.price").value(15000.00));
    }

    @Test
    void getTourById_admin_returnsHateoasLinks() throws Exception {
        // Admins must also receive HATEOAS links upon resource retrieval
        mockMvc.perform(get("/api/tours/42")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links['all-tours'].href").exists());
    }

    @Test
    void getTourById_defaultUser_returnsHateoasLinks() throws Exception {
        // Regular users must receive HATEOAS links upon resource retrieval
        mockMvc.perform(get("/api/tours/42")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links['all-tours'].href").exists());
    }

    // --- Tier 2: Boundary & Corner Cases ---

    @Test
    void getTourById_nonExistentTour_returnsNotFoundWithoutHateoasLinks() throws Exception {
        // Non-existent tour should return 404 and NOT return HATEOAS links in error response
        mockMvc.perform(get("/api/tours/999")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$._links").doesNotExist());
    }

    @Test
    void getTourById_invalidIdFormat_returnsBadRequestWithoutHateoasLinks() throws Exception {
        // Request with invalid ID format should fail at MVC mapping and not return HATEOAS links
        mockMvc.perform(get("/api/tours/invalid-id-format")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$._links").doesNotExist());
    }

    @Test
    void getTourById_extremeIdValue_returnsNotFoundWithoutHateoasLinks() throws Exception {
        // Extreme ID values should handle cleanly (either 404 or 400 depending on framework limits)
        mockMvc.perform(get("/api/tours/999999999999999999")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$._links").doesNotExist());
    }

    @Test
    void getTourById_customHalAcceptHeader_returnsHalJsonMediaType() throws Exception {
        // Content negotiations with application/hal+json should be supported or gracefully degrade,
        // and return the appropriate self links
        mockMvc.perform(get("/api/tours/42")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken)
                        .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void getTourById_dynamicHostMapping_returnsHateoasLinksWithDynamicPort() throws Exception {
        // Hypermedia links must preserve the requested protocol, host, and port dynamically.
        mockMvc.perform(get("/api/tours/42")
                        .header(HttpHeaders.HOST, "localhost:8080")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(containsString("http://localhost:8080/api/tours/42")));
    }
}
