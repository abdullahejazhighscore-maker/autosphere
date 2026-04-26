package com.samtech.carapp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {
    @Test
    void hashShouldBeDeterministicAndValid() {
        String hash1 = PasswordUtil.hashPassword("buyer123");
        String hash2 = PasswordUtil.hashPassword("buyer123");
        assertEquals(hash1, hash2);
        assertTrue(PasswordUtil.matches("buyer123", hash1));
        assertFalse(PasswordUtil.matches("wrong-pass", hash1));
    }
}
