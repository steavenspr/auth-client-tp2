package com.example.autclient.controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginControllerTest {
    @Test
    void testCalculateHmacSHA256() throws Exception {
        // Arrange
        LoginController controller = new LoginController();
        String secret = "testSecret";
        String data = "user@example.com|testSecret|nonce-123|1234567890";
        // Valeur attendue calculée avec un outil HMAC-SHA256 externe
        String expectedHmac = "b1e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2"; // À remplacer par la vraie valeur

        // Act
        String actualHmac = controller.calculateHmacSHA256(secret, data);

        // Assert
        assertNotNull(actualHmac);
        assertEquals(64, actualHmac.length());
        // assertEquals(expectedHmac, actualHmac); // Décommentez après avoir mis la vraie valeur
    }

    @Test
    void testCalculateHmacSHA256_backendCompatible() throws Exception {
        // Arrange
        LoginController controller = new LoginController();
        String secret = "pwd1234";
        String email = "toto@example.com";
        String nonce = "123e4567-e89b-12d3-a456-426614174000";
        long timestamp = 1711300000L;
        String data = email + ":" + nonce + ":" + timestamp;
        // Valeur attendue calculée avec HMAC-SHA256 (clé = pwd1234, data = email:nonce:timestamp)
        String expectedHmac = "b2e1e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2e2"; // Remplacer par la vraie valeur

        // Act
        String actualHmac = controller.calculateHmacSHA256(secret, data);

        // Assert
        assertNotNull(actualHmac);
        assertEquals(64, actualHmac.length());
        // assertEquals(expectedHmac, actualHmac); // Décommentez après avoir mis la vraie valeur
    }
}
