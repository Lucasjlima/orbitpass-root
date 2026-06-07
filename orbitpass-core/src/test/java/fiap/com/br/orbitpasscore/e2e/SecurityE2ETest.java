package fiap.com.br.orbitpasscore.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityE2ETest extends BaseE2ETest {

    // =========================================================================
    // FEATURE 1: Swagger UI & API Docs accessibility (10 Tests)
    // =========================================================================

    // --- Tier 1: Feature Coverage (Happy Path) ---

    @Test
    void swaggerUiIndex_returnsSuccessWithoutAuth() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void v3ApiDocs_returnsSuccessWithoutAuth() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void v3ApiDocsSwaggerConfig_returnsSuccessWithoutAuth() throws Exception {
        mockMvc.perform(get("/v3/api-docs/swagger-config"))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerResources_returnsSuccessWithoutAuth() throws Exception {
        // May return 200 or 404 depending on how it's configured in SpringDoc, but must NOT return 401/403
        mockMvc.perform(get("/swagger-resources"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Expected non-secured status, but got " + status);
                    }
                });
    }

    @Test
    void webjarsSwaggerUi_returnsSuccessWithoutAuth() throws Exception {
        // May return 200 or 404, but must NOT return 401/403
        mockMvc.perform(get("/webjars/swagger-ui/index.css"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Expected non-secured status, but got " + status);
                    }
                });
    }

    // --- Tier 2: Boundary & Corner Cases ---

    @Test
    void swaggerUiRoot_returnsRedirectOrSuccessWithoutAuth() throws Exception {
        // Accessing /swagger-ui/ should redirect or load, not return 401/403
        mockMvc.perform(get("/swagger-ui/"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Expected redirect or success, but got " + status);
                    }
                });
    }

    @Test
    void v3ApiDocsWithMethodPost_returnsMethodNotAllowedWithoutAuth() throws Exception {
        // Whitelisted endpoint accessed with wrong method. Should be 405 or 404, but NOT 401/403
        mockMvc.perform(post("/v3/api-docs"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Expected 405 or 404, but got " + status);
                    }
                });
    }

    @Test
    void swaggerResourcesWithMethodPost_returnsMethodNotAllowedWithoutAuth() throws Exception {
        // Whitelisted endpoint accessed with wrong method. Should be 405 or 404, but NOT 401/403
        mockMvc.perform(post("/swagger-resources"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Expected 405 or 404, but got " + status);
                    }
                });
    }

    @Test
    void webjarsNonExistentResource_returnsNotFoundWithoutAuth() throws Exception {
        // A non-existent resource in whitelisted path should return 404, NOT 401/403
        mockMvc.perform(get("/webjars/swagger-ui/non-existent.css"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Expected 404, but got " + status);
                    }
                });
    }

    @Test
    void v3ApiDocsSwaggerConfigWithTrailingSlash_returnsNotFoundWithoutAuth() throws Exception {
        // Accessing /v3/api-docs/swagger-config/ with trailing slash should be 404, not 401/403
        mockMvc.perform(get("/v3/api-docs/swagger-config/"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Expected 404, but got " + status);
                    }
                });
    }

    // =========================================================================
    // FEATURE 2: API Endpoints Security (10 Tests)
    // =========================================================================

    // --- Tier 1: Feature Coverage (Happy Path) ---

    @Test
    void getUsers_anonymous_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUsers_defaultUser_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTour_anonymous_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/tours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Space Tour\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTour_defaultUser_returnsForbidden() throws Exception {
        mockMvc.perform(post("/api/tours")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Space Tour\",\"description\":\"Desc\",\"destination\":\"Space\",\"price\":100.0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void seedVectorStore_defaultUser_returnsForbidden() throws Exception {
        mockMvc.perform(post("/api/vector-store/seed")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isForbidden());
    }

    // --- Tier 2: Boundary & Corner Cases ---

    @Test
    void getUserProfile_accessOtherUser_returnsForbidden() throws Exception {
        // User with ID 2 trying to access user profile of ID 99
        mockMvc.perform(get("/api/users/99")
                        .header(HttpHeaders.AUTHORIZATION, defaultToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidToken_returnsUnauthorized() throws Exception {
        // Sending invalid token should reject with 401
        mockMvc.perform(get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalidtokenhere"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void malformedToken_returnsUnauthorized() throws Exception {
        // Sending header with missing Bearer prefix or custom prefix should reject with 401
        mockMvc.perform(get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, "Token " + defaultToken.substring(7)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void nonExistentEndpoint_anonymous_returnsUnauthorized() throws Exception {
        // A non-existent endpoint under secured hierarchy should return 401, not 404, for anonymous requests
        mockMvc.perform(get("/api/non-existent-secured-endpoint"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void securedPostWithValidationErrors_anonymous_returnsUnauthorized() throws Exception {
        // Accessing a secured endpoint with invalid data as anonymous should still return 401 (blocked at filter)
        // instead of 400 (validation error handled by controller)
        mockMvc.perform(post("/api/tours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
