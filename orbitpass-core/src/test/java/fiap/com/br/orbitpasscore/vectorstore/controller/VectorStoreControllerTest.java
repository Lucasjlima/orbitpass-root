package fiap.com.br.orbitpasscore.vectorstore.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fiap.com.br.orbitpasscore.security.SecurityConfig;
import fiap.com.br.orbitpasscore.security.SecurityFilter;
import fiap.com.br.orbitpasscore.vectorstore.service.VectorStoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.ContextConfiguration;

@WebMvcTest(
        controllers = VectorStoreController.class,
        excludeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, SecurityFilter.class}))
@ContextConfiguration(classes = {VectorStoreController.class, fiap.com.br.orbitpasscore.OrbitpassCoreApplication.class})
@AutoConfigureMockMvc(addFilters = false)
class VectorStoreControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VectorStoreService vectorStoreService;

    @Test
    void chat_returns200_whenValidRequest() throws Exception {
        when(vectorStoreService.chat(anyString())).thenReturn("Mars has cardiac restrictions.");

        mvc.perform(post("/api/vector-store/chat")
                        .contentType(APPLICATION_JSON)
                        .content("{\"message\":\"What are Mars restrictions?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Mars has cardiac restrictions."));
    }

    @Test
    void chat_returns400_whenMessageBlank() throws Exception {
        mvc.perform(post("/api/vector-store/chat")
                        .contentType(APPLICATION_JSON)
                        .content("{\"message\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void chat_returns400_whenMessageTooLong() throws Exception {
        String longMessage = "a".repeat(1001);

        mvc.perform(post("/api/vector-store/chat")
                        .contentType(APPLICATION_JSON)
                        .content("{\"message\":\"" + longMessage + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void seed_returns200() throws Exception {
        mvc.perform(post("/api/vector-store/seed"))
                .andExpect(status().isOk());
    }
}
