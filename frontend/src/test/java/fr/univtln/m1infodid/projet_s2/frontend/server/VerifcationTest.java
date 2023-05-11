package fr.univtln.m1infodid.projet_s2.frontend.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerifcationTest {

    @Test
    void testValidID(){
        Assertions.assertTrue(Verifcation.isInputOnlyInteger("1"));
    }

    @Test
    void testValidEmail(){
        Assertions.assertTrue(Verifcation.isInputAvalideEmail("theo@gmail.com"));
    }

    @Test
    void testNotValidEmail(){
        Assertions.assertFalse(Verifcation.isInputAvalideEmail("theo!gmail.com"));
    }

    @Test
    void testNotValidID(){
        Assertions.assertFalse(Verifcation.isInputOnlyInteger("!"));
    }

}