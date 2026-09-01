package com.pethub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethub.dto.request.LoginRequest;
import com.pethub.dto.request.RegisterRequest;
import com.pethub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserRegistrationSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Ananya",
                "Patel",
                "ananya.test@example.com",
                "Password@123",
                "9876543299"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("ananya.test@example.com"))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    public void testDuplicateEmailRegistrationFails() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Duplicate",
                "User",
                "customer@pethub.com", // existing seed email
                "Password@123",
                "9876543299"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("customer@pethub.com", "User@123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    public void testLoginBadPasswordFails() throws Exception {
        LoginRequest request = new LoginRequest("customer@pethub.com", "WrongPassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
