package fr.univtln.m1infodid.projet_s2.backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FormulaireTest {
    Formulaire formulaire1 = Formulaire.of(1, "Ben", "Rawi", "benrawi@mail.com", "test","gp A", "commentaire 1");
    @Test
     void testConstructorAndGetters() {
        assertEquals(1, formulaire1.getId());
        assertEquals("Ben", formulaire1.getNom());
        assertEquals("Rawi", formulaire1.getPrenom());
        assertEquals("benrawi@mail.com", formulaire1.getEmail());
        assertEquals("gp A", formulaire1.getAffiliation());
        assertEquals("commentaire 1", formulaire1.getCommentaire());
    }
    @Test
    void testEqualsDifferentType() {
        String str = "not a Formulaire object";
        assertNotEquals(formulaire1, str);
    }
    @Test
     void testSetter() {
        Formulaire formulaire = new Formulaire();
        formulaire.setId(2);
        formulaire.setNom("ben");
        formulaire.setPrenom("raw");
        formulaire.setEmail("ben.raw@example.com");
        formulaire.setAffiliation("univ tln");
        formulaire.setCommentaire("okok");

        assertEquals(2, formulaire.getId());
        assertEquals("ben", formulaire.getNom());
        assertEquals("raw", formulaire.getPrenom());
        assertEquals("ben.raw@example.com", formulaire.getEmail());
        assertEquals("univ tln", formulaire.getAffiliation());
        assertEquals("okok", formulaire.getCommentaire());
    }
    @Test
    void testEqualsEmptyFormulaire(){
        Formulaire formulaire2 = new Formulaire();
        assertNotEquals(formulaire2, formulaire1);

    }


    @Test
    void testEqualsRST() {
        Formulaire formulaire2 = Formulaire.of(1, "Ben", "Myr", "benmyr@mail.com", "test", "gp A", "Commentaire 2");
        Formulaire formulaire3 = Formulaire.of(1, "Ben", "Rawi", "benrawi@mail.com", "test", "gp A", "Commentaire 1");
        assertEquals(formulaire2, (formulaire3));
    }

    @Test
     void testToString() {
        String expected = "Demande inscription: n°1 pour le visiteur Rawi Ben d'adresse mail benrawi@mail.com et d'affiliation gp A.\ncommentaire 1";
        assertEquals(expected, formulaire1.toString());
    }
}





