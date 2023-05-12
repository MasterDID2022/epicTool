package fr.univtln.m1infodid.projet_s2.frontend.frontend;

import fr.univtln.m1infodid.projet_s2.frontend.server.Api;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiTest {

    /*
    Cette méthode teste la méthode sendRequestOf() de la classe Api en envoyant une requête avec un ID valide.
    *Elle vérifie que le résultat n'est pas nul.
     */
    @Test
    public void testSendRequestOf() {
        Integer id = 1;
        String result = Api.sendRequestOf(id);
        Assertions.assertNotNull(result);
    }

    /*
    *teste la méthode sendRequestOf() de la classe Api en envoyant une requête avec un ID invalide.
    *Elle vérifie que le résultat est une chaîne vide.
     */
    @Test
    public void testSendRequestOfWithInvalidId() {
        Integer id = -1;
        String result = Api.sendRequestOf(id);
        assertEquals("", result);
    }

    /*
    *teste la méthode postAnnotations() de la classe Api en envoyant une chaîne JSON d'annotations vide.
    *Elle vérifie que le résultat n'est pas nul.
     */
    @Test
    public void testPostAnnotations() {
        String annotationsJson = "{\"annotations\": []}";
        String result = Api.postAnnotations(annotationsJson);
        Assertions.assertNotNull(result);
    }

    /*
    *teste la méthode postAnnotations() de la classe Api en envoyant une chaîne JSON d'annotations invalide.
    *Elle vérifie que le résultat est une chaîne vide.
     */
    @Test
    public void testPostAnnotationsWithInvalidJson() {
        String annotationsJson = "{\"annotations\" []}";
        String result = Api.postAnnotations(annotationsJson);
        assertEquals("", result);
    }

    /*
     *teste la méthode postAnnotations() de la classe Api en envoyant une chaîne JSON d'annotations nulle.
     *Elle vérifie que le résultat est une chaîne vide.
     */
    @Test
    public void testPostAnnotationsWithNullJson() {
        String annotationsJson = null;
        String result = Api.postAnnotations(annotationsJson);
        assertEquals("", result);
    }

}
