package com.dwi.expensetracker;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestAuthUtil {
    public static String obtainJwtToken(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String username,
            String password)
            throws Exception {
        // mock keycloak token endpoint
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("client_id", "expense-tracker-api");
        loginPayload.put("username", username);
        loginPayload.put("password", password);
        loginPayload.put("grant_type", "password");
        loginPayload.put("scope", "openid profile roles");

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/realms/expense-realm/protocol/openid-connect/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(toFormUrlEncoded(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("access_token").asText();
    }

    private static String toFormUrlEncoded(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    // util to create a mock JWT for unit tests
    public static String createMockJwt(String userId, String username, String role) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("sub", userId);
        claims.put("preferred_username", username);
        claims.put("roles", Collections.singletonList(role));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("http://localhost:8080/realms/expense-realm")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hour
                .signWith(SignatureAlgorithm.HS256, "test-secret")
                .compact();
    }
}
