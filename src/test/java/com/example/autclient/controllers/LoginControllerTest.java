package com.example.autclient.controllers;

import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private static final String EXPECTED_HMAC_BASIC = "f2cf5da010ab5bae16a9051a031a360ae800b7c358698efe0e71aa0dba6bdfb4";
    private static final String EXPECTED_HMAC_BACKEND_COMPATIBLE = "7a15c84c3ef80528634a5edd2774f0721f2d94439b704e72a8c931d72191846c";

    @Test
    void testCalculateHmacSHA256() throws NoSuchAlgorithmException, InvalidKeyException {
        LoginController controller = new LoginController();
        String secret = "testSecret";
        String data = "user@example.com|testSecret|nonce-123|1234567890";

        String actualHmac = controller.calculateHmacSHA256(secret, data);

        assertNotNull(actualHmac);
        assertEquals(64, actualHmac.length());
        assertEquals(EXPECTED_HMAC_BASIC, actualHmac);
    }

    @Test
    void testCalculateHmacSHA256_backendCompatible() throws NoSuchAlgorithmException, InvalidKeyException {
        LoginController controller = new LoginController();
        String secret = "pwd1234";
        String email = "toto@example.com";
        String nonce = "123e4567-e89b-12d3-a456-426614174000";
        long timestamp = 1711300000L;
        String data = email + ":" + nonce + ":" + timestamp;

        String actualHmac = controller.calculateHmacSHA256(secret, data);

        assertNotNull(actualHmac);
        assertEquals(64, actualHmac.length());
        assertEquals(EXPECTED_HMAC_BACKEND_COMPATIBLE, actualHmac);
    }
}
