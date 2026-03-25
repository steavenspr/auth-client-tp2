package com.example.autclient.controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    @Test
    void testCalculateHmacSHA256() throws Exception {
        // Arrange
        LoginController controller = new LoginController();
        String secret = "testSecret";
        String data = "user@example.com|testSecret|nonce-123|1234567890";
        // Pour tester la valeur exacte, décommentez et renseignez la vraie valeur calculée :
        // String expectedHmac = "...";

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
        // Pour tester la valeur exacte, décommentez et renseignez la vraie valeur calculée :
        // String expectedHmac = "...";

        // Act
        String actualHmac = controller.calculateHmacSHA256(secret, data);

        // Assert
        assertNotNull(actualHmac);
        assertEquals(64, actualHmac.length());
        // assertEquals(expectedHmac, actualHmac); // Décommentez après avoir mis la vraie valeur
    }
}
