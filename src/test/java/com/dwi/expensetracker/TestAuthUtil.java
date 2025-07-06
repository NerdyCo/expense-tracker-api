package com.dwi.expensetracker;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestAuthUtil {

        private static KeycloakContainer keycloakContainer;

        public static void setKeycloakContainer(KeycloakContainer container) {
                keycloakContainer = container;
        }

        public static String obtainJwtToken(
                        MockMvc mockMvc,
                        ObjectMapper objectMapper,
                        String username,
                        String password,
                        String clientId,
                        String clientSecret) throws Exception {
                if (keycloakContainer == null)
                        throw new IllegalStateException("KeycloakContainer must be set before obtaining tokens");

                String tokenEndpoint = keycloakContainer.getAuthServerUrl()
                                + "/realms/expense-realm/protocol/openid-connect/token";
                HashMap<String, String> loginPayload = new HashMap<>();
                loginPayload.put("client_id", clientId);
                loginPayload.put("client_secret", clientSecret);
                loginPayload.put("username", username);
                loginPayload.put("password", password);
                loginPayload.put("grant_type", "password");
                loginPayload.put("scope", "openid profile roles");

                try {
                        MvcResult result = mockMvc
                                        .perform(MockMvcRequestBuilders.post(tokenEndpoint)
                                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                        .content(toFormUrlEncoded(loginPayload)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.access_token").exists())
                                        .andReturn();

                        String response = result.getResponse().getContentAsString();
                        JsonNode jsonNode = objectMapper.readTree(response);
                        return jsonNode.get("access_token").asText();
                } catch (Exception e) {
                        throw new RuntimeException("Failed to obtain JWT token for user: " + username, e);
                }

        }

        private static String toFormUrlEncoded(Map<String, String> params) {
                return params.entrySet().stream()
                                .map(entry -> entry.getKey() + "="
                                                + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                                .collect(Collectors.joining("&"));
        }
}
