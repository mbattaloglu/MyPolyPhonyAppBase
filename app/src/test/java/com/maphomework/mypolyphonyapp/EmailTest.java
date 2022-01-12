package com.maphomework.mypolyphonyapp;


import org.junit.Test;
import static org.junit.Assert.*;

public class EmailTest {
    @Test
    public void validEmailWithTrue() {
        assertTrue(EmailChecker.isValidEmail("user@email.com"));
    }

    @Test
    public void invalidEmailWithTrue() {
        assertTrue(EmailChecker.isValidEmail("user@email"));
    }

    @Test
    public void validEmailWithFalse() {
        assertFalse(EmailChecker.isValidEmail("user@email.com"));
    }

    @Test
    public void invalidEmailWithFalse() {
        assertFalse(EmailChecker.isValidEmail("user@email"));
    }


}
