package com.pethub;

import com.pethub.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void testPublicEndpointsAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/pet-categories"))
                .andExpect(status().isOk());
    }

    @Test
    public void testProtectedEndpointRejectsUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCustomerCannotAccessAdminEndpoint() throws Exception {
        // Generate Customer Token
        String customerToken = jwtTokenProvider.generateTokenFromUserId(2L, "customer@pethub.com");

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAdminCanAccessAdminEndpoint() throws Exception {
        // Generate Admin Token
        String adminToken = jwtTokenProvider.generateTokenFromUserId(1L, "admin@pethub.com");

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
