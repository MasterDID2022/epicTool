package fr.univtln.m1infodid.projet_s2.frontend.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerificationTest {

    @Test
    void testValidID(){
        Assertions.assertTrue(Verification.isInputOnlyInteger("1"));
    }

    @Test
    void testValidEmail(){
        Assertions.assertTrue(Verification.isInputAvalideEmail("theo@gmail.com"));
    }

    @Test
    void testNotValidEmail(){
        Assertions.assertFalse(Verification.isInputAvalideEmail("theo!gmail.com"));
    }

    @Test
    void testNotValidID(){
        Assertions.assertFalse(Verification.isInputOnlyInteger("!"));
    }

}